# RxJavaTwo-HurryUp
ReactiveX y RxJava pintan un amplio trazo contra muchos problemas que enfrentan los programadores
todos los días, lo que le permite expresar la lógica comercial 
y dedicar menos tiempo a la ingeniería de código.

¿Alguna vez ha tenido problemas con la concurrencia, 
el manejo de eventos, los estados de datos obsoletos 
y la recuperación de excepciones?


¿Qué tal hacer que su código sea más mantenible, reutilizable y 
evolucionable para que pueda seguir el ritmo de su negocio?

Puede ser presuntuoso llamar a la programación reactiva una panacea para estos problemas, 
pero ciertamente es un salto progresivo para abordarlos.
La programación reactiva le permite analizar y trabajar rápidamente con fuentes de datos en vivo, 
como feeds de Twitter o precios de acciones. También puede cancelar y redirigir el trabajo, 
escalar con concurrencia y hacer frente a la emisión rápida de datos.
La composición de eventos y datos como flujos que se pueden mezclar, fusionar, 
filtrar, dividir y transformar abre formas radicalmente efectivas de componer 
y desarrollar código.
En resumen, la programación reactiva facilita muchas tareas difíciles, lo que le permite agregar valor 
en formas que antes podría haber considerado poco prácticas.

## ¿Cuando usar RxJava?
La primera respuesta es cuando empiezas: ¡sí! Siempre quieres adoptar un enfoque reactivo.
La única forma de convertirse verdaderamente en un maestro de la programación reactiva es crear 
aplicaciones reactivas desde cero.
Piense en todo como Observable y siempre modele su programa en términos de datos y
flujos de ventilación. Cuando haga esto, aprovechará todo lo que la programación reactiva tiene para ofrecer 
y verá que la calidad de sus aplicaciones aumenta significativamente.
La segunda respuesta es que cuando tenga experiencia en RxJava, 
encontrará casos en los que RxJava puede no ser apropiado.
Ocasionalmente, habrá momentos en los que un enfoque reactivo puede no ser óptimo, pero, por lo general, 
esta excepción se aplica solo a una parte de su código.
Todo su proyecto en sí debe ser reactivo. Puede haber partes que no sean reactivas y por una buena razón.
Estas excepciones solo se destacan para un veterano capacitado en Rx que ve que devolver 
`List<String>` es quizás mejor que devolver `Observable<String>`.

## Una exposición rápida a RxJava
Antes de sumergirnos profundamente en el mundo reactivo de RxJava, 
aquí hay una exposición rápida para mojarse los pies primero. 
En ReactiveX, el tipo de núcleo con el que trabajará es el Observable. 
Aprenderemos más sobre el Observable a lo largo del resto de este libro.
Pero esencialmente, un Observable empuja las cosas.
Un `Observable<T>` dado empuja cosas de tipo T a través de una serie de operadores 
hasta que llega a un Observador que consume los elementos.

