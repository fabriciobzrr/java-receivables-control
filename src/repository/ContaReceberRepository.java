package repository;

import entities.ContaReceber;
import entities.enums.CategoriaReceita;
import entities.enums.StatusContaReceber;
import exception.ContaNaoEncontradaException;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ContaReceberRepository {
    private final List<ContaReceber> contasReceber = new ArrayList<>();

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final String CAMINHO_ARQUIVO = "dados/contas_receber.csv";

    public ContaReceberRepository() {
        carregarDoArquivo();
    }

    public void adicionarContaReceber(ContaReceber contaReceber) {
        contasReceber.add(contaReceber);
        salvarNoArquivo();
    }

    public List<ContaReceber> listarContasReceber() {
        return new ArrayList<>(contasReceber);
    }

    public ContaReceber buscaContaPorId(int id) {
        return contasReceber.stream()
                .filter(contaReceber -> contaReceber.getId() == id)
                .findFirst()
                .orElseThrow( () -> new ContaNaoEncontradaException("Conta de ID " + id + " nao encontrada!"));
    }

    public void removerContaPorId(int id) {
        ContaReceber contaReceber = buscaContaPorId(id);
        contasReceber.remove(contaReceber);
        salvarNoArquivo();
    }

    public void atualizarConta(ContaReceber contaReceber) {
        removerContaPorId(contaReceber.getId());
        adicionarContaReceber(contaReceber);
    }

    // Persistência
    private void salvarNoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        File diretorio = arquivo.getParentFile();

        if (diretorio != null && !diretorio.exists()) {
            diretorio.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {

            bw.write("id;valor;dataVencimento;cliente;status;categoria;dataRecebimento;descricao");
            bw.newLine();

            for (ContaReceber contaReceber : contasReceber) {
                bw.write(converterParaCSV(contaReceber));
                bw.newLine();
            }

        } catch (IOException err) {
            System.err.println("Erro ao salvar o arquivo: " + err.getMessage());
        }
    }

    private void carregarDoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();  // Lê e DESCARTAR cabeçalho

            while ((linha = br.readLine()) != null) {  // Começa da segunda linha
                ContaReceber conta = converterDoCSV(linha);
                if (conta != null) contasReceber.add(conta);
            }
        } catch (IOException err) {
            System.err.println("Erro ao carregar: " + err.getMessage());
        }
    }

    private String converterParaCSV(ContaReceber contaReceber) {
        String dataRecebimento = contaReceber.getDataRecebimento() != null ? DTF.format(contaReceber.getDataRecebimento()) : "";

        return String.format("%d;%s;%s;%s;%s;%s;%s;%s",
                contaReceber.getId(),
                String.format("%.2f", contaReceber.getValor()).replace(",", "."),
                DTF.format(contaReceber.getDataVencimento()),
                contaReceber.getCliente(),
                contaReceber.getStatus().name(),
                contaReceber.getCategoria().name(),
                dataRecebimento,
                contaReceber.getDescricao());
    }

    private ContaReceber converterDoCSV(String linha) {
        try {
            String[] campo = linha.split(";");


            int id = Integer.parseInt(campo[0]);
            String valorStr = campo[1].replace(",", ".");
            double valor = Double.parseDouble(valorStr);

            LocalDate dataVencimento = LocalDate.parse(campo[2], DTF);
            String cliente = campo[3];
            StatusContaReceber status = StatusContaReceber.valueOf(campo[4]);
            CategoriaReceita categoria = CategoriaReceita.valueOf(campo[5]);
            LocalDate dataRecebimento = campo[6].isBlank() ? null : LocalDate.parse(campo[6], DTF);
            String descricao = campo[7];

            ContaReceber contaReceber = new ContaReceber(descricao, valor, dataVencimento, categoria, cliente);

            contaReceber.setId(id);

            contaReceber.setStatus(status);

            if (dataRecebimento != null) {
                contaReceber.receberConta(dataRecebimento);
            }

            return contaReceber;
        } catch (Exception err) {
            System.err.println("Erro ao converter linha: " + err.getMessage());
            return null;
        }

    }
}
