package entities.enums;

public enum OpcoesMenu {
    CADASTRAR("Cadastrar Conta a Receber"),
    RECEBER("Baixar Recebimento"),
    ESTORNAR("Estornar Recebimento"),
    LISTAR("Listar Todas as Contas a Receber"),
    FILTRAR_STATUS("Filtrar Contas a Receber por Status"),
    FILTRAR_PERIODO("Filtrar Contas a Receber por Período"),
    ALERTAS("Ver Contas a Receber em Atraso"),
    RESUMO("Ver Resumo Geral de Contas a Receber");

    private String descricao;

    OpcoesMenu(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
