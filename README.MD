# Aprofundando estudos em máquina de estados

Neste projeto realizei a criação de uma máquina de estados simples de pagamento utilizando Spring State Machine. Neste problema resolvido a propostar era realizar a devolução de uma transaçäo de pagamento, após um estado finalizador ser acionado. Para isso, utilizei Regiões e Composite State para incluir o fluxo de devoluçäo em uma região separada.
Aproveitei para documentar um pouco dessa aventura e conceitos absorvidos durante o caminho neste repositório. Junto com as referencias utilizadas.
Apesar da minha stack principal ser PHP, deicidi utilizar o Spring State Machine, para fortificar meus conhecimentos no framework, e principalmente pois eles fornecem um rico material na documentação do framework.
No futuro pretendo realizar o mesmo teste com a máquina de estados do Symfony e fazer um comparativo. 

Aproveitando para deixar meus 10 centavos de contribuição quanto a ferramenta, achei o Spring State Machine muito facil de ser utilizado, além de ter uma documentação fantástica e completa explicando todos os conceitos de máquina de estados.

O objetivo aqui, não é repassar como foi criada a máquina de estados, afinal, todos somos devs e podemos consultar a documentação e entender o que o código está fazendo. O objetivo deste repositório foi aprender os conceitos de máquina de estados para que possa realizar algo mais complexo, e repassar esses conceitos.

Espero poder auxiliar alguém com este resumo.

Este desenho abaixo, foi realizado utilizando PlantUML, quem quiser [ver um pouco mais](docs/stateuml.txt), colocando em prática outro conhecimento adquirido nesta semana, na querida Weekly de PHP do PicPay <3

![plot](docs/umlestados)

# Objetivo de uma máquina de estados

O objetivo de uma máquina de estado é abstrair o controle de estados, de modo que não seja necessário realizar diversas verificações, criando ifs/elses ou switch/case desencessários, ou lógicas complexas. Podemos realizar isso apenas confiando nas iterações com a máquina de estados, como eventos, transiçöes e states. 

Esta ferramenta pode ser muito potente para diversos casos, como por exemplo, um pedido em uma loja de e-commerce, pode passar por diversos estados. Com a máquina de estados, conseguimos realizar o controle de todos os estados que um pedido pode passar, desde a criação do pedido até a entrega, apenas definindo as configuraçöes da sua máquina de estado. E realizando a chamada dos eventos para que sejam realizadas as transições, se quisermos ir mais além, conseguimos definir ACTIONS para cada vez que um Evento ou uma transition for chamada. Além de também nos permitir que tenhamos Listeners para os States e diversas outras funcionalidades.
Vamos falar um pouco mais sobre esses termos agora:


# Conceitos:

### Máquina de estados
Entidade principal que coordena uma coleção de estados junto a regiões, transições e eventos.

### Estados

Os estados são um modelo do que uma máquina de estado pode ser. O objetivo dos estados é a utilização de flags, sem se torturar com if/elses e switch/cases. Apenas confiando na troca de estados, variações de estados e interações com a máquina de estados.

### Pseudo-estados (Pseudo States)

Um pseudo estado é um estado que introduz uma lógica de alto nível dentro da máquina de estados, dando um significado especial a ele. Uma máquina de estados pode reagir internamente de diversas formas, realizando ações disponiveis nos conceitos de UML.

- Initial State:

É um estado necessário para todas as máquinas de estado. O pseudoestado Initial faz com que a máquina de estado se inicie.

- End State: 

O pseudoestado final indica que o fluxo atingiu um estado que irá encerrar o fluxo. Neste caso, o fluxo não mudará mais de estado. No entanto, no caso onde submachines são regions uma máquina de estado pode restartar a partir deste estado finalizador.

- Choice State:


Você pode utilizar o choice para escolher uma ramificação condicional dinâmica de uma transição de estado. A condição dinamica é avaliada pelos guards para que uma ramificação seja selecionada.
Normalmente uma estrutura if/else é utilizada para garantir que uma ramificação seja selecionada. Caso contrário, a máquina poderá acabando em deadlock e a configuração está mal formada.

- Junction State 

Semelhante ao Choice State, porém permite varias transições de entrada enquanto choice permite apenas uma.

- History State:

Um pseudo estado que permite que a máquina de estados se lembre do último estado ativo. Existem dois tipos de history states, Shallow (que relembra apenas o estado de nivel superior) e deep (que relembra todos os estados ativos na máquina de estados)

- Fork State: 
 
Estado que controla a entrada em uma região.
  
- Join State: 
  
Controla a saida de uma região.

- Entry Point:

Pseudo estado que controla a entrada de uma sub-machine.

- Exit Point:

Pseudo estado que controla a saida de uma sub-machine.

- Region:

É uma parte de um composite state ou de uma máquina de estado. Este contém estados e transições.
Exemplo: https://docs.spring.io/spring-statemachine/docs/current/reference/index.html#regions

- Guard:

É uma expressão booleana avaliada dinâmicamente com base no valor de "state extended variables" e parâmetros de eventos. Os Guards podem afetar o comportamento da máquina de estados habilitando ações e transições apenas quando for true e desabilitando quando for false.

- Actions
Ações são comportamentos executados durante a trigger de uma transition.

### Referências
https://docs.spring.io/spring-statemachine/docs/current/reference/index.html#state-machine-concepts


