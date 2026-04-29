# 💰 Contas a Receber – Sistema Java

Sistema de gerenciamento de contas a receber com persistência em CSV.

## 🎯 Funcionalidades

- Cadastrar, listar, receber, estornar
- Filtros por status e período
- Resumo financeiro
- Alerta de contas vencidas

## 🏗️ Estrutura
```
src/
├── application/Main.java
├── entities/
│ ├── ContaReceber.java
│ └── enums/
│ ├── CategoriaReceita.java
│ ├── OpcoesMenu.java
│ └── StatusContaReceber.java
├── services/ContaReceberService.java
├── repository/ContaReceberRepository.java
└── exceptions/
├── ContaNaoEncontradaException.java
└── ValorInvalidoException.java

dados/contas_receber.csv
```


## 🛠️ Tecnologias

Java 25 | CSV | IntelliJ

## 🚀 Como executar

1. Clone o repositório
2. Execute `Main.java`
3. Menu no console

## 👨‍💻 Autor

Fabricio Bezerra
