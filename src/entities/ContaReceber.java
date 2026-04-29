package entities;

import entities.enums.CategoriaReceita;
import entities.enums.StatusContaReceber;
import exception.ValorInvalidoException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ContaReceber {
    private static int contadorId = 1;

    private int id;
    private String descricao;
    private double valor;
    private LocalDate dataVencimento;
    private LocalDate dataRecebimento;
    private StatusContaReceber status;
    private CategoriaReceita categoria;
    private String cliente;

    public ContaReceber(String descricao, double valor, LocalDate dataVencimento, CategoriaReceita categoria, String cliente) {
        if (descricao.isBlank()) {
            throw new ValorInvalidoException("Campo descrição obrigatório!");
        }
        if (valor <= 0) {
            throw new ValorInvalidoException("Valor deve ser positivo!");
        }
        if (dataVencimento == null) {
            throw new ValorInvalidoException("Campo data de vencimento obrigatório!");
        }
        if (cliente.isBlank()) {
            throw new ValorInvalidoException("Campo cliente obrigatório!");
        }

        this.id = contadorId++;
        this.descricao = descricao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.dataRecebimento = null;
        this.status = StatusContaReceber.PENDENTE;
        this.categoria = categoria;
        this.cliente = cliente;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public LocalDate getDataRecebimento() {
        return dataRecebimento;
    }

    public StatusContaReceber getStatus() {
        return status;
    }

    public CategoriaReceita getCategoria() {
        return categoria;
    }

    public String getCliente() {
        return cliente;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValor(double valor) {
        if (valor <= 0) {
            throw new ValorInvalidoException("Valor não pode ser negativo!");
        }
        this.valor = valor;
    }

    public void setStatus(StatusContaReceber status) {
        this.status = status;
    }

    public void setDescricao(String descricao) {
        if (descricao.isBlank()) {
            throw new ValorInvalidoException("Campo descrição obrigatório!");
        }
        this.descricao = descricao;
    }

    public void setCliente(String cliente) {
        if (cliente.isBlank()) {
            throw new ValorInvalidoException("Campo cliente obrigatório!");
        }
        this.cliente = cliente;
    }

    public void setCategoria(CategoriaReceita categoria) {
        if (categoria == null) {
            throw new ValorInvalidoException("Campo categoria obrigatório!");
        }
        this.categoria = categoria;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new ValorInvalidoException("Campo data de vencimento obrigatório!");
        }
        this.dataVencimento = dataVencimento;
    }

    public void receberConta(LocalDate dataRecebimento) {
        if (dataRecebimento == null) {
            throw new ValorInvalidoException("Campo data de pagamento obrigatório!");
        }
        this.dataRecebimento = dataRecebimento;
        this.status = StatusContaReceber.RECEBIDA;
    }

    public void estornarRecebimento() {
        this.status = StatusContaReceber.ESTORNO;
    }

    public void atualizarStatusRecebimento(LocalDate dataAtual) {
        if (this.status == StatusContaReceber.ESTORNO) return;

        if (this.status == StatusContaReceber.RECEBIDA) return;

        if (this.dataVencimento.isBefore(dataAtual)) {
            this.status = StatusContaReceber.VENCIDA;
        } else {
            this.status = StatusContaReceber.PENDENTE;
        }

    }

    @Override
    public String toString(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String nomeClienteCurto = cliente.length() > 15 ? cliente.substring(0, 12) + "..." : cliente;
        String descricaoCurta = descricao.length() > 20 ? descricao.substring(0, 14) + "..." : descricao;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| #%d | R$ %.2f | %s | %s | %s | %s | %s |",
                id,
                valor,
                dtf.format(dataVencimento),
                nomeClienteCurto,
                status.getDescricao(),
                categoria.getDescricao(),
                descricaoCurta));

        if (dataRecebimento != null) {
            sb.append(String.format(" Recebido: %s |", dtf.format(dataRecebimento)));
        }
        return sb.toString();
    }
}
