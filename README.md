# Telegram ChatGPT Bot

Этот проект представляет собой Telegram-бота, имеющего возможность использовать все модели ChatGPT, включая последнюю ChatGPT-4o-mini. Однако функционал реализован только под текстовые модели. Возможно, чуть позже будет добавлена поддержка DALL_E_3 и т. п.

Бот автоматически обрабатывает сообщения пользователей и сжимает текст беседы, чтобы обеспечить оптимальную производительность и взаимодействие. Если выбрана модель ниже ChatGPT-3.5-instruct, бот переключается на старый режим обработки входящего текста. Идет перевод текста с выбранного языка на английский с помощью YandexTranslate, затем запрос передается на английском языке в OpenAi, а возвращаемый ответ уходит обратно в YandexTranslate для перевода на выбранный язык и последующего отправления в Telegram-чат, откуда и был сделан запрос. Это сделано для того, чтобы старые модели, которые преимущественно обучались на английских текстах, могли отвечать качественно на вашем языке, иначе они могут показаться слабоватыми.

## Попробуйте мою реализацию

1. Нажмите или введите:  
   `/start`
   
2. Затем бот спросит пароль, введите:  
   `пароль`
   
3. Затем бот попросит ввести имя. Можете придумать что-нибудь смешное и начинать пользоваться :)

   [Начать чат с ботом](https://t.me/KJLUM_bot)

## Стек технологий

- **Язык программирования**: Java
- **Библиотеки**:
    - Telegram Bots API   
    - Hibernate
    - YandexTranslate API
    - OpenAI API               
- **СУБД**: SQL (например, PostgreSQL, MySQL)

## Описание функциональности

- **Хранение контекста беседы**: Бот хранит историю общения с пользователем в реляционной базе данных, что позволяет сохранять контекст для более качественного взаимодействия.
- **Сжатие текста**: По умолчанию, каждые 3 сообщения бот автоматически сжимает текст беседы в 1.5 раза для оптимизации хранения и обработки. Эти константы можно настраивать под ваши нужды.
- **Конфигурация контекста**: По умолчанию, бот хранит контекст длиной 6500 символов и/или 15 сообщений (где запрос пользователя и ответ бота считаются одним сообщением).

## Структура базы данных

### Таблица: users_chats

| Поле            | Тип            | Описание                                   |
| --------------- | -------------- | ------------------------------------------ |
| CHAT_ID        | BIGINT         | Уникальный идентификатор чата              |
| TEXT           | TEXT           | Хранение текста беседы                     |
| MESSAGE_COUNT  | DECIMAL(0, 15) | Количество сообщений в текущем контексте   |
| USER_NAME      | VARCHAR        | Имя пользователя                           |
| VERSION        | INT            | Версия записи (для управления изменениями) |

## Зависимости

Для сборки и запуска проекта необходимы следующие зависимости Maven:

```xml
<dependencies>
    <dependency>
        <groupId>com.theokanning.openai-gpt3-java</groupId>
        <artifactId>client</artifactId>
        <version>0.18.2</version>
    </dependency>
    <dependency>
        <groupId>com.theokanning.openai-gpt3-java</groupId>
        <artifactId>service</artifactId>
        <version>0.18.2</version>
    </dependency>
    <dependency>
        <groupId>com.theokanning.openai-gpt3-java</groupId>
        <artifactId>api</artifactId>
        <version>0.18.2</version>
    </dependency>
    <dependency>
        <groupId>com.googlecode.json-simple</groupId>
        <artifactId>json-simple</artifactId>
        <version>1.1.1</version>
    </dependency>
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20180813</version>
    </dependency>
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.15.4</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots</artifactId>
        <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.15</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.4.2.Final</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-longpolling</artifactId>
        <version>7.7.1</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-extensions</artifactId>
        <version>7.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-client</artifactId>
        <version>7.7.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.inject</groupId>
        <artifactId>guice</artifactId>
        <version>5.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-meta</artifactId>
        <version>7.7.1</version>
    </dependency>
</dependencies>
```

## Установка и настройка

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/IamKJLUM/Telegram-OpenAi-YandexTranslate.git
   cd telegram-chatgpt-bot
   ```
   
2. Настройте базу данных и внесите параметры подключения в конфигурационный файл.
3. Соберите проект:
   ```bash
   mvn clean install
   ```
   
4. Запустите бота:
   ```bash
   java -jar target/telegram-chatgpt-bot.jar
   ```
   
5. Зарегистрируйте бота в Telegram и получите токен.
6. Получите новый [oAuth token](https://oauth.yandex.ru/authorize?response_type=token&client_id=1a6990aa636648e9b2ef855fa7bec2fb) от YandexCloud.
7. Настройте переменные окружения с токеном:
   ```bash
   export TOKEN_BOT=your_bot_token_telegram_here
   export TOKEN_OPENAI=your_bot_token_open_ai_here
   export FOLDER_ID=your_folder_id
   export O_AUTH_TOKEN=your_o_auth_token
   ```

## Использование

- После запуска бот будет принимать сообщения от пользователей и поддерживать беседу с учетом сохраненного контекста.
- Для получения помощи отправьте команду `/help`.

## Лицензия

Этот проект лицензируется на условиях MIT License.

## Контрибьюция

Если вы хотите внести свой вклад, пожалуйста, создайте pull request или откройте issue с предложениями!

## Автор
Клим / KJLUM
