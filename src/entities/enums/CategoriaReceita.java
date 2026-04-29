package entities.enums;

public enum CategoriaReceita {
    VENDAS_PRODUTOS("Vendas de Produtos"),
    PRESTACAO_SERVICOS("Prestação de Serviços"),
    MENSALIDADES_ASSINATURAS("Mensalidades e Assinaturas"),
    COMISSOES_RECEBIDAS("Comissões Recebidas"),
    REEMBOLSOS("Reembolsos"),
    RECEITAS_FINANCEIRAS("Receitas Financeiras"),
    JUROS_MULTAS_RECEBIDOS("Juros e Multas Recebidos"),
    ALUGUEIS_RECEBIDOS("Aluguéis Recebidos"),
    VENDAS_ATIVOS("Venda de Ativos"),
    ADIANTAMENTOS_CLIENTES("Adiantamentos de Clientes"),
    PARCELAMENTOS_CLIENTES("Parcelamentos de Clientes"),
    CARTAO_CREDITO("Recebimentos via Cartão de Crédito"),
    BOLETO_BANCARIO("Recebimentos via Boleto"),
    TRANSFERENCIAS_PIX_TED("Transferências (PIX, TED, DOC)"),
    CHEQUE("Cheque"),
    OUTRAS_RECEITAS("Outras Receitas");

    private String descricao;

    CategoriaReceita (String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
