# iakta-social

### Esercizio colloquio iakta
L'applicativo è composto da un modulo Java Spring Boot + database embedded H2 che simula MySql.
Viene eseguito in maniera serverless da Google Cloud Run, collegato a questo repository Github per CD.

Per un elenco di tutti gli endpoint esposti, visitare l'url
 - **swagger url:** /swagger-ui/index.html

La creazione dell'utente viene effettuata mediante endpoint /signup
Le password sono hashate con BCrypt

Il login viene effettuato con l'endpoint /signin e gestito con JWT
  
 - **h2 db url:** /h2-console

 - **h2 db username:** davide