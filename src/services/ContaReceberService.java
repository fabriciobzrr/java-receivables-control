package services;

import entities.ContaReceber;
import entities.enums.StatusContaReceber;
import repository.ContaReceberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ContaReceberService {

    private ContaReceberRepository contaReceberRepository;

    public ContaReceberService() {
        this.contaReceberRepository = new ContaReceberRepository();
    }

    public void cadastrarContaReceber(ContaReceber contaReceber) {
        if (contaReceber == null) {
            throw new IllegalArgumentException("Conta não pode ser nula!");
        }
        if (contaReceber.getValor() <= 0) {
            throw new IllegalArgumentException("Valor da conta não pode ser negativo!");
        }

        contaReceberRepository.adicionarContaReceber(contaReceber);
    }

    public List<ContaReceber> listarContasReceber() {
       return contaReceberRepository.listarContasReceber();
    }

    public ContaReceber buscarContaPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido!");
        }
        return contaReceberRepository.buscaContaPorId(id);
    }

    public void receberConta (int id, LocalDate dataPagamento) {
        ContaReceber contaReceber = buscarContaPorId(id);

        if (dataPagamento == null) {
            throw new IllegalArgumentException("Data de pagamento não pode ser nula!");
        }

        if (contaReceber.getStatus() == StatusContaReceber.RECEBIDA) {
            throw new IllegalStateException("Conta já recebida!");
        }

        contaReceber.receberConta(dataPagamento);
        contaReceberRepository.atualizarConta(contaReceber);
    }

    public void estornarConta(int id) {
        ContaReceber contaReceber = buscarContaPorId(id);
        if (contaReceber.getStatus() == StatusContaReceber.ESTORNO) {
            throw new IllegalStateException("Conta já estornada!");
        }
        contaReceber.estornarRecebimento();
        contaReceberRepository.atualizarConta(contaReceber);
    }

    public List<ContaReceber> filtrarPorStatus(StatusContaReceber status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo!");
        }
        return listarContasReceber().stream()
                .filter(contaReceber -> contaReceber.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<ContaReceber> filtrarPorPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Data inicial e data final não podem ser nulas!");
        }
        if (dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior a data final!");
        }
        return listarContasReceber().stream()
                .filter(contaReceber -> {
                    LocalDate vencimento = contaReceber.getDataVencimento();
                    return !vencimento.isBefore(dataInicial) && !vencimento.isAfter(dataFinal);
                }).collect(Collectors.toList());
    }

    public List<ContaReceber> listarContasVencidas() {
        return filtrarPorStatus(StatusContaReceber.VENCIDA);
    }

    public double totalContasPendentes() {
        return listarContasReceber().stream()
                .filter(contaReceber -> contaReceber.getStatus() == StatusContaReceber.PENDENTE)
                .mapToDouble(ContaReceber::getValor)
                .sum();
    }

    public void atualizarStatusContas (LocalDate dataAtual) {
        if (dataAtual == null) {
            throw new IllegalArgumentException("Data atual não pode ser nula!");
        }

        for (ContaReceber conta : contaReceberRepository.listarContasReceber()) {
            StatusContaReceber statusAntigo = conta.getStatus();
            conta.atualizarStatusRecebimento(dataAtual);

            StatusContaReceber statusAtual = conta.getStatus();
            if (statusAntigo != statusAtual) {
                contaReceberRepository.atualizarConta(conta);
            }
        }
    }

}
