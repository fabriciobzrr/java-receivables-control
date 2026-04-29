package entities.enums;

public enum StatusContaReceber {
    RECEBIDA("Recebida"),
    PENDENTE("Pendente"),
    VENCIDA("Vencida"),
    ESTORNO("Estornada");

    private String descricao;

    StatusContaReceber (String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
