# CanaBot
Bot de Telegram para gestionar el grupo del Departamento de Informática del I.E.S. Canarias.

## Ejecución

Descargamos el código fuente del bot y entramos dentro:

```bash
git clone https://github.com/iescanarias/CanaBot.git
cd CanaBot
```

Lo compilamos:

```java
mvn compile
```

Creamos el fichero `config.json` en el directorio raíz de la aplicación con el siguiente contenido:

```json
{
  "bot": {
    "token": "<token de autorización del bot asignado por @BotFather>",
    "botname": "<nombre de usuario del bot>",
    "chatId": <identificador del grupo donde el bot puede actuar>
  },
  "email": {
    "smtp": {
      "host": "<nombre del servidor smtp>",
      "port": <puerto del servidor smtp>
    },
    "username": "<email>",
    "password": "<password>"
  },
  "subscribers": [ <suscritos>, ... ],
  "admins": [ <administradores del bot>, ...]
}
```

E iniciamos el bot:

```bash
mvn exec:java
```

> Si queremos iniciarlo en segundo plano en GNU/Linux:
>
> ```bash
> nohup exec:java &
> ```
