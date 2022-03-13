# Aprofundando estudos em máquina de estados

## Conceitos:

### Estados

Os estados são um modelo do que uma máquina de estado pode ser... O objetivo dos estados é a utilização de flags, sem se torturar com if/elses e switch/cases. Apenas confiando na troca de estados, variações de estados e interações com a máquina de estados.

### Pseudo-estados (Pseudo States)

Um pseudo estado é um estado que introduz uma lógica de alto nível dentro da máquina de estados, dando um significado especial a ele. Uma máquina de estados pode reagir internamente de diversas formas, realizando ações disponiveis nos conceitos de UML.

- Initial
É um estado necessário para todas as máquinas de estado. O pseudoestado Initial faz com que a máquina de estado se inicie.

- End
O pseudoestado final indica que o fluxo atingiu um estado que irá encerrar o fluxo. Neste caso, o fluxo não mudará mais de estado. No entanto, no caso onde submachines são regions uma máquina de estado pode restartar a partir deste estado finalizador.

- Choice
Você pode utilizar o choice para escolher uma ramificação condicional dinâmica de uma transição de estado. A condição dinamica é avaliada pelos guards para que uma ramificação seja selecionada.
Normalmente uma estrutura if/else é utilizada para garantir que uma ramificação seja selecionada. Caso contrário, a máquina poderá acabando em deadlock e a configuração está mal formada.

![plot](docs/umlestados)
