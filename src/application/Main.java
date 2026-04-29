package application;

import entities.ContaReceber;
import entities.enums.CategoriaReceita;
import entities.enums.OpcoesMenu;
import entities.enums.StatusContaReceber;
import exception.ContaNaoEncontradaException;
import services.ContaReceberService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DTF_BRAZIL = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ContaReceberService contaReceberService = new ContaReceberService();

    public static void main(String[] args) {
        contaReceberService.atualizarStatusContas(LocalDate.now());

        IO.println("======== SISTEMA DE CONTAS A RECEBER ========");

        int opcao;

        do {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opção: ".toUpperCase());

            switch (opcao)  {
                case 1 -> cadastrarConta();
                case 2 -> receberConta();
                case 3 -> estornarConta();
                case 4 -> listarContas();
                case 5 -> filtrarPorPeriodo();
                case 6 -> filtrarPorStatus();
                case 7 -> alertarVencidas();
                case 8 -> mostrarResumo();
                case 0 -> IO.println("\nEncerrando aplicação...");
                default -> IO.println("\nOpção inválida! Tente novamente.");
            }
        } while (opcao != 0);

    }

    private static void mostrarMenu() {
        IO.println("\n-------- MENU PRINCIPAL CONTAS A RECEBER --------");
        int count = 1;
        for (OpcoesMenu opcoes : OpcoesMenu.values()) {
            IO.println(count + " - " + opcoes.getDescricao().toUpperCase());
            count++;
        }
        IO.println("0 - ENCERRAR\n");
    }

    // Auxiliares
    private static String lerTexto(String msg) {
        IO.print(msg);

        String texto = sc.nextLine();

        if(texto.isBlank()) {
            IO.println("\nCampo não pode estar vazio!");
            return "";
        }

        return texto;
    }

    private static int lerInteiro(String msg) {
        IO.print(msg);

        while (!sc.hasNextInt()) {
            IO.println("\nValor inválido! Digite um valor válido!");
            IO.print("Digite novamente: ");
            sc.next();
        }

        int valor = sc.nextInt();
        sc.nextLine();

        return valor;
    }

    private static double lerDouble(String msg) {
        IO.print(msg);

        while (!sc.hasNextDouble()) {
            IO.println("\nValor inválido! Digite um valor válido!");
            IO.print("Digite novamente: ");
            sc.next();
        }

        double valor = sc.nextDouble();
        sc.nextLine();

        return valor;
    }

    private static void mostrarCategorias() {
        IO.println("\nEscolha uma categoria:");
        int count = 1;
        for (CategoriaReceita categoria : CategoriaReceita.values()) {
            IO.println(count + " - " + categoria.getDescricao().toUpperCase());
            count++;
        }
    }

    private static LocalDate lerData(String msg, boolean obrigatorio) {
        IO.print(msg);
        String dataTexto = sc.nextLine();
        if (dataTexto.isBlank()) {
            if (obrigatorio) {
                IO.println("Campo data não pode estar vazio!");
                return lerData(msg, obrigatorio);
            } else {
                String data = DTF_BRAZIL.format(LocalDate.now());
                IO.println("Data: " + data);
                return LocalDate.now();
            }
        }

        try {
            return LocalDate.parse(dataTexto, DTF_BRAZIL);

        } catch (DateTimeParseException err) {
            IO.println("\nData inválida! Use o formato DD/MM/AAAA!");
            return lerData(msg, obrigatorio);
        }
    }

    private static boolean confirmar(String msg) {
        IO.print(msg);
        String opcao = sc.nextLine();

        if (opcao.isBlank()) {
            IO.println("\nOpção inválida!");
            return false;
        }

        char opcaoSelecionada = opcao.toUpperCase().charAt(0);

        return opcaoSelecionada == 'S';
    }

    // Principais
    public static void cadastrarConta() {
        IO.println("\n-------- CADASTRAR CONTAS A RECEBER --------");

        try {
            String descricao = lerTexto("Descrição: ");
            if (descricao.isBlank()) {
                return;
            }

            double valor = lerDouble("Valor: R$ ");
            if (valor <= 0) {
                IO.println("\nValor não pode ser menor ou igual a zero!");
                return;
            }

            LocalDate dataVencimento = lerData("Data de vencimento (DD/MM/AAAA): ", true);

            mostrarCategorias();

            int opcaoCategoria = lerInteiro("\nEscolha uma categoria: ");
            if (opcaoCategoria < 1 || opcaoCategoria > CategoriaReceita.values().length) {
                IO.println("\nCategoria inválida! Escolha uma categoria entre 1 e " + CategoriaReceita.values().length + "!");
                return;
            }

            CategoriaReceita categoria = CategoriaReceita.values()[opcaoCategoria - 1];

            String cliente = lerTexto("Cliente: ");
            if (cliente.isBlank()) {
                IO.println("\nCampo cliente não pode estar vazio!");
                return;
            }

            ContaReceber contaReceber = new ContaReceber(descricao, valor, dataVencimento, categoria, cliente);

            IO.println("\n-------- Resumo da Conta A Receber --------");
            IO.println(contaReceber.toString());

            if (!confirmar("\nConfirmar cadastramento de conta a receber? (S/N) ")) {
                IO.println("\nCadastro cancelado!");
                cadastrarConta();
            } else {
                contaReceberService.cadastrarContaReceber(contaReceber);

                IO.println("\nConta a receber cadastrada com sucesso!");
            }

            if (confirmar("\nDeseja cadastrar uma nova conta a receber? (S/N) ")) {
                cadastrarConta();
            } else {
                IO.println("\nRetornando ao menu principal!");
            }
        } catch (Exception err) {
            IO.println("\nErro ao cadastrar a conta: " + err.getMessage());
        }
    }

    public static void listarContas() {
        List<ContaReceber> contasReceber = contaReceberService.listarContasReceber();
        IO.println("\n-------- LISTAR TODAS AS CONTAS A RECEBER --------");

        if (contasReceber.isEmpty()) {
            IO.println("\nNenhum registro de contas a receber encontrado!");
            return;
        }

        for (ContaReceber contaReceber : contasReceber) {
            IO.println(contaReceber.toString());
        }

        IO.println("\nTotal de registros: " + contasReceber.size());
    }

    public static void receberConta() {
        IO.println("\n-------- BAIXAR RECEBIMENTO DE CONTAS A RECEBER --------");

        try {
            int id = lerInteiro("\nDigite o ID da conta: ");

            ContaReceber conta = contaReceberService.buscarContaPorId(id);

            if (conta.getStatus() == StatusContaReceber.RECEBIDA) {
                IO.println("Baixa de conta já realizada!");
                return;
            }

            IO.println("\nDados da conta:");
            IO.println(conta.toString());

            if (!confirmar("\nA conta acima está correta? (S/N) ")) {
                IO.println("\nBaixa de recebimento cancelada!");
                receberConta();
            }

            LocalDate dataPagamento = lerData("\nData da baixa de recebimento (DD/MM/AAAA) ou Enter para hoje: ", false);

            if (!confirmar("\nDeseja realmente baixar esta conta? (S/N) ")) {
                IO.println("\nBaixa de recebimento cancelada!");
                return;
            }

            contaReceberService.receberConta(id, dataPagamento);
            IO.println("\nRecebimento realizado com sucesso!");

            if (confirmar("\nDeseja baixar outra conta? (S/N) ")) {
                receberConta();
            } else {
                IO.println("\nRetornando ao menu principal!");
            }

        } catch (ContaNaoEncontradaException err) {
            IO.println("\nErro ao baixar conta: " + err.getMessage());
        }
    }

    public static void filtrarPorStatus() {
        IO.println("\n-------- FILTRAR CONTAS A RECEBER POR STATUS --------");

        List<ContaReceber> contas = contaReceberService.listarContasReceber();

        IO.println("\nEscolha uma opção de status:");
        int count = 1;
        for (StatusContaReceber status : StatusContaReceber.values()) {
            IO.println(count + " - " + status.getDescricao().toUpperCase());
            count++;
        }
        IO.println("0 - Voltar");

        int opcaoStatus = lerInteiro("\nEscolha uma opção: ");
        StatusContaReceber status = null;

        switch (opcaoStatus) {
            case 1 -> status = StatusContaReceber.PENDENTE;
            case 2 -> status = StatusContaReceber.RECEBIDA;
            case 3 -> status = StatusContaReceber.VENCIDA;
            case 4 -> status = StatusContaReceber.ESTORNO;
            case 0 -> { return; }
            default -> IO.println("Opção inválida! Escolha uma opção entre 1 e " + StatusContaReceber.values().length + " ou 0 para sair!");
        }

        List<ContaReceber> filtradas = contaReceberService.filtrarPorStatus(status);

        if (filtradas.isEmpty()) {
            IO.println("\nNenhum registro de contas a receber " + status.getDescricao().toLowerCase() + "s encontrado!");
            return;
        }

        IO.println("\nContas a receber com status: " + status.getDescricao().toUpperCase());

        for (ContaReceber conta : filtradas) {
            IO.println(conta.toString());
        }

        IO.println("\nTotal de Registros: " + filtradas.size());
    }

    public static void mostrarResumo() {
        IO.println("\n-------- RESUMO FINANCEIRO DE CONTAS A RECEBER --------");
        double pendentes = contaReceberService.totalContasPendentes();
        double vencidas = contaReceberService.listarContasVencidas().stream()
                .mapToDouble(ContaReceber::getValor).sum();

        double totalContas = pendentes + vencidas;

        IO.println("\nTotal " + StatusContaReceber.PENDENTE.getDescricao() + ": R$ " + real(pendentes));
        IO.println("Total de " + StatusContaReceber.VENCIDA.getDescricao() + "s: R$ " + real(vencidas));
        IO.println("Total de contas a receber: R$ " + real(totalContas));

    }

    private static String real(double valor) {
        return String.format("%.2f", valor);
    }

    public static void filtrarPorPeriodo() {
        IO.println("\n-------- FILTRAR CONTAS A RECEBER POR PERÍODO --------");

        LocalDate dataInicial = lerData("Data inicial (DD/MM/AAAA): ", true);
        LocalDate dataFinal = lerData("Data final (DD/MM/AAAA): ", true);

        List<ContaReceber> contasFiltradas = contaReceberService.filtrarPorPeriodo(dataInicial, dataFinal);

        if (contasFiltradas.isEmpty()) {
            IO.println("Nenhuma registro de contas a receber encontrado para o período de " + DTF_BRAZIL.format(dataInicial) + " à " + DTF_BRAZIL.format(dataFinal) + "!");
            return;
        }

        for (ContaReceber conta : contasFiltradas) {
            IO.println(conta);
        }

        IO.println("\nTotal de registros: " + contasFiltradas.size());
    }

    public static void alertarVencidas() {
        IO.println("\n-------- Alerta de Contas Vencidas --------");

        List<ContaReceber> vencidas = contaReceberService.listarContasVencidas();

        if (vencidas.isEmpty()) {
            IO.println("Nenhum registro de contas a receber " + StatusContaReceber.VENCIDA.getDescricao().toLowerCase() + "s encontrado!");
            return;
        }

        for (ContaReceber conta : vencidas) {
            IO.println(conta);
        }

        IO.println("\nTotal de registros: " + vencidas.size());

    }

    public static void estornarConta() {
        IO.println("\n-------- ESTORNO DE CONTAS A RECEBER --------");

        try {
            int id = lerInteiro("\nDigite o ID da conta: ");

            ContaReceber contaReceber = contaReceberService.buscarContaPorId(id);

            if (contaReceber.getStatus() == StatusContaReceber.ESTORNO) {
                IO.println("A conta acima está estornada!");
                return;
            }

            IO.println("\nDados da conta a receber:");
            IO.println(contaReceber);

            if(!confirmar("\nDeseja estornar a baixa desta conta a receber? (S/N) ")) {
                IO.println("\nEstorno cancelado!");
                estornarConta();
            }

            contaReceberService.estornarConta(id);

            IO.println("Estorno realizado com sucesso!");

        } catch (ContaNaoEncontradaException err) {
            IO.println("\nErro ao estornar a conta a receber: " + err.getMessage());
        }
    }
}