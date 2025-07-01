import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Peita {

    static class Usuario {
        private String email;
        private String senha;
        private Carrinho carrinho;
        private boolean isAdmin;

        public Usuario(String email, String senha, boolean isAdmin) {
            this.email = email;
            this.senha = senha;
            this.carrinho = new Carrinho();
            this.isAdmin = isAdmin;
        }

        public String getEmail() {
            return email;
        }

        public boolean validarSenha(String senha) {
            return this.senha.equals(senha);
        }

        public Carrinho getCarrinho() {
            return carrinho;
        }

        public boolean isAdmin() {
            return isAdmin;
        }
    }

    static class Produto {
        private int id;
        private String nome;
        private double preco;

        public Produto(int id, String nome, double preco) {
            this.id = id;
            this.nome = nome;
            this.preco = preco;
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public double getPreco() {
            return preco;
        }

        @Override
        public String toString() {
            return nome + " - R$" + String.format("%.2f", preco);
        }
    }

    static class Carrinho {
        private Map<Produto, Integer> itens = new HashMap<>();

        public void adicionarProduto(Produto produto, int quantidade) {
            itens.put(produto, itens.getOrDefault(produto, 0) + quantidade);
        }

        public void removerProduto(Produto produto, int quantidade) {
            if (itens.containsKey(produto)) {
                int quantidadeAtual = itens.get(produto);
                if (quantidadeAtual <= quantidade) {
                    itens.remove(produto);
                } else {
                    itens.put(produto, quantidadeAtual - quantidade);
                }
            }
        }

        public double calcularTotal() {
            double total = 0;
            for (Map.Entry<Produto, Integer> entry : itens.entrySet()) {
                total += entry.getKey().getPreco() * entry.getValue();
            }
            return total;
        }

        public void exibirCarrinho() {
            if (itens.isEmpty()) {
                System.out.println("\nSeu carrinho está vazio.");
                return;
            }

            System.out.println("\n=== SEU CARRINHO ===");
            for (Map.Entry<Produto, Integer> entry : itens.entrySet()) {
                System.out.println(entry.getKey().getNome() + 
                                 " x " + entry.getValue() + 
                                 " = R$" + String.format("%.2f", entry.getKey().getPreco() * entry.getValue()));
            }
            System.out.println("TOTAL: R$" + String.format("%.2f", calcularTotal()));
            System.out.println("====================");
        }
    }

    private static Map<String, Usuario> usuarios = new HashMap<>();
    private static List<Produto> catalogo = new ArrayList<>();

    public static void main(String[] args) {
        inicializarDados();
        Scanner scanner = new Scanner(System.in);
        Usuario usuarioLogado = null;

        System.out.println("=== BEM-VINDO À PEITA STORE ===");

        while (true) {
            if (usuarioLogado == null) {
                System.out.println("\n1. Login");
                System.out.println("2. Cadastrar novo usuário");
                System.out.println("3. Sair");
                System.out.print("Escolha uma opção: ");
                
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        usuarioLogado = fazerLogin(scanner);
                        break;
                    case 2:
                        cadastrarUsuario(scanner);
                        break;
                    case 3:
                        System.out.println("Obrigado por visitar a Peita Store!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            } else {
                System.out.println("\nOlá, " + usuarioLogado.getEmail() + 
                                 (usuarioLogado.isAdmin() ? " (Administrador)" : "") + "!");
                
                System.out.println("1. Ver catálogo de produtos");
                System.out.println("2. Adicionar produto ao carrinho");
                System.out.println("3. Ver carrinho");
                System.out.println("4. Remover produto do carrinho");
                System.out.println("5. Finalizar compra");
                
                if (usuarioLogado.isAdmin()) {
                    System.out.println("6. Menu Administrativo");
                    System.out.println("7. Sair");
                } else {
                    System.out.println("6. Sair");
                }
                
                System.out.print("Escolha uma opção: ");
                
                int opcao = scanner.nextInt();
                scanner.nextLine();

                if (usuarioLogado.isAdmin() && opcao == 6) {
                    System.out.println("\n=== MENU ADMINISTRATIVO ===");
                    System.out.println("1. Adicionar novo produto");
                    System.out.println("2. Remover produto existente");
                    System.out.println("3. Listar todos os produtos");
                    System.out.println("4. Voltar ao menu principal");
                    System.out.print("Escolha uma opção: ");
                    
                    int opcaoAdmin = scanner.nextInt();
                    scanner.nextLine();
                    
                    switch (opcaoAdmin) {
                        case 1:
                            adicionarProduto(scanner);
                            break;
                        case 2:
                            removerProduto(scanner);
                            break;
                        case 3:
                            exibirCatalogo();
                            break;
                        case 4:
                            break;
                        default:
                            System.out.println("Opção inválida!");
                    }
                    continue;
                }
                
                if ((usuarioLogado.isAdmin() && opcao == 7) || 
                    (!usuarioLogado.isAdmin() && opcao == 6)) {
                    System.out.println("Logout realizado com sucesso!");
                    usuarioLogado = null;
                    continue;
                }

                switch (opcao) {
                    case 1:
                        exibirCatalogo();
                        break;
                    case 2:
                        adicionarAoCarrinho(scanner, usuarioLogado);
                        break;
                    case 3:
                        usuarioLogado.getCarrinho().exibirCarrinho();
                        break;
                    case 4:
                        removerDoCarrinho(scanner, usuarioLogado);
                        break;
                    case 5:
                        finalizarCompra(usuarioLogado);
                        usuarioLogado = null;
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            }
        }
    }

    private static void inicializarDados() {
        usuarios.put("cliente@peita.com", new Usuario("cliente@peita.com", "senha123", false));
        usuarios.put("admin@peita.com", new Usuario("admin@peita.com", "admin123", true));
        
        catalogo.add(new Produto(1, "Camiseta Brasil", 149.90));
        catalogo.add(new Produto(2, "Camiseta Corinthians", 149.90));
        catalogo.add(new Produto(3, "Camiseta Barcelona", 149.90));
        catalogo.add(new Produto(4, "Conjunto Esportivo", 299.90));
        catalogo.add(new Produto(5, "Camiseta Real Madrid", 149.90));
        catalogo.add(new Produto(6, "Camiseta Palmeiras", 149.90));
        catalogo.add(new Produto(7, "Camiseta Flamengo", 149.90));
        catalogo.add(new Produto(8, "Camiseta PSG", 149.90));
        catalogo.add(new Produto(9, "Camiseta Milan", 149.90));
        catalogo.add(new Produto(10, "Camiseta Espanha", 149.90));
    }

    private static Usuario fazerLogin(Scanner scanner) {
        System.out.print("\nEmail: ");
        String email = scanner.nextLine();
        
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        
        if (usuarios.containsKey(email)) {
            Usuario usuario = usuarios.get(email);
            if (usuario.validarSenha(senha)) {
                System.out.println("\nLogin realizado com sucesso!");
                return usuario;
            }
        }
        
        System.out.println("\nEmail ou senha inválidos!");
        return null;
    }

    private static void cadastrarUsuario(Scanner scanner) {
        System.out.print("\nEmail: ");
        String email = scanner.nextLine();
        
        if (usuarios.containsKey(email)) {
            System.out.println("Email já cadastrado!");
            return;
        }
        
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        
        usuarios.put(email, new Usuario(email, senha, false));
        System.out.println("\nUsuário cadastrado com sucesso!");
    }

    private static void exibirCatalogo() {
        System.out.println("\n=== CATÁLOGO DE PRODUTOS ===");
        for (Produto produto : catalogo) {
            System.out.println(produto.getId() + ". " + produto);
        }
        System.out.println("===========================");
    }

    private static void adicionarAoCarrinho(Scanner scanner, Usuario usuario) {
        exibirCatalogo();
        System.out.print("\nDigite o ID do produto: ");
        int id = scanner.nextInt();
        
        System.out.print("Quantidade: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();
        
        Produto produtoSelecionado = null;
        for (Produto produto : catalogo) {
            if (produto.getId() == id) {
                produtoSelecionado = produto;
                break;
            }
        }
        
        if (produtoSelecionado != null) {
            usuario.getCarrinho().adicionarProduto(produtoSelecionado, quantidade);
            System.out.println("\nProduto adicionado ao carrinho!");
        } else {
            System.out.println("\nProduto não encontrado!");
        }
    }

    private static void removerDoCarrinho(Scanner scanner, Usuario usuario) {
        Carrinho carrinho = usuario.getCarrinho();
        carrinho.exibirCarrinho();
        
        if (carrinho.itens.isEmpty()) {
            return;
        }
        
        System.out.print("\nDigite o ID do produto a ser removido: ");
        int id = scanner.nextInt();
        
        System.out.print("Quantidade: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();
        
        Produto produtoSelecionado = null;
        for (Produto produto : catalogo) {
            if (produto.getId() == id) {
                produtoSelecionado = produto;
                break;
            }
        }
        
        if (produtoSelecionado != null && carrinho.itens.containsKey(produtoSelecionado)) {
            carrinho.removerProduto(produtoSelecionado, quantidade);
            System.out.println("\nProduto removido do carrinho!");
        } else {
            System.out.println("\nProduto não encontrado no carrinho!");
        }
    }

    private static void finalizarCompra(Usuario usuario) {
        Carrinho carrinho = usuario.getCarrinho();
        
        if (carrinho.itens.isEmpty()) {
            System.out.println("\nSeu carrinho está vazio. Adicione produtos antes de finalizar a compra!");
            return;
        }
        
        System.out.println("\n=== COMPRA FINALIZADA ===");
        carrinho.exibirCarrinho();
        System.out.println("Obrigado por comprar na Peita Store!");
        System.out.println("==========================");
        
        carrinho.itens.clear();
    }

    private static void adicionarProduto(Scanner scanner) {
        System.out.println("\n--- ADICIONAR NOVO PRODUTO ---");
        
        System.out.print("Nome do produto: ");
        String nome = scanner.nextLine();
        
        double preco = 0;
        boolean precoValido = false;
        while (!precoValido) {
            System.out.print("Preço: R$");
            try {
                preco = scanner.nextDouble();
                scanner.nextLine();
                if (preco > 0) {
                    precoValido = true;
                } else {
                    System.out.println("O preço deve ser maior que zero!");
                }
            } catch (Exception e) {
                System.out.println("Valor inválido! Digite um número (ex: 99.90)");
                scanner.nextLine();
            }
        }
        
        int novoId = catalogo.isEmpty() ? 1 : catalogo.get(catalogo.size() - 1).getId() + 1;
        
        Produto novoProduto = new Produto(novoId, nome, preco);
        catalogo.add(novoProduto);
        
        System.out.println("\nProduto adicionado com sucesso!");
        System.out.println("ID: " + novoId + " | " + nome + " - R$" + String.format("%.2f", preco));
    }

    private static void removerProduto(Scanner scanner) {
        exibirCatalogo();
        
        if (catalogo.isEmpty()) {
            System.out.println("\nNão há produtos cadastrados!");
            return;
        }
        
        System.out.print("\nDigite o ID do produto a ser removido: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        Produto produtoParaRemover = null;
        for (Produto produto : catalogo) {
            if (produto.getId() == id) {
                produtoParaRemover = produto;
                break;
            }
        }
        
        if (produtoParaRemover != null) {
            catalogo.remove(produtoParaRemover);
            System.out.println("\nProduto removido com sucesso: " + produtoParaRemover.getNome());
        } else {
            System.out.println("\nProduto não encontrado com ID: " + id);
        }
    }
}