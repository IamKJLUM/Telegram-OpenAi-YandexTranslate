# Telegram ChatGPT Bot

Этот проект представляет собой Telegram-бота, имеющего возможность использовать все модели ChatGPT включая последнюю ChatGPT-4o-mini, но функционал реализован только под текстовые модели, возможно чуть позже добавлю поддержку DALL_E_3 и тп. 
Для хранения контекста беседы используется реляционная база данных SQL.
Бот автоматически обрабатывает сообщения пользователей и сжимает текст беседы, чтобы обеспечить оптимальную производительность и взаимодействие.
	Так же если выбрана модель ниже ChatGPT-3.5-instruct то бот переключается на старый режим обработки входящего текста. Идет перевод текста с выбранного языка на английский, с помощью YandexTranslate, затем запрос передается на английском языке в OpenAi, возвращающийся ответ уходит обратно в YandexTranslate для перевода на выбранный язык и последующее отправление в телеграмм чат от куда и был сделан запрос.
		 Сделано это для того-чтобы старые модели которые преимущественно обучались на английских текстах могли отвечать качественно на вашем языке в ином случае они кажутся слабоватыми. 

## Попробуйте мою реализацию

- **Нажмите или введите**:
	`/start`

- **Затем бот спросит пароль введите:**
	пароль

- **Затем бот попросит ввести имя, можете 
	придумать что-нибудь смешное и начинать пользоваться :)**
	
	https://t.me/KJLUM_bot
	
## Стек технологий

- **Язык программирования**: Java
- **Библиотеки**: 
  - Telegram Bots API   >telegrambots-longpolling<
  - Hibernate (ORM для работы с реляционными базами данных)
  - YandexTranslate API
  - OpenAi API               >com.theokanning.openai-gpt3-java<
- **СУБД**: SQL (например, PostgreSQL, MySQL)

## Описание функциональности

- **Хранение контекста беседы**: Бот хранит историю общения с пользователем в реляционной базе данных, что позволяет сохранять контекст для более качественного взаимодействия.
- **Сжатие текста**: По умолчанию, каждые 3 сообщения бот автоматически сжимает текст беседы в 1.5 раза для оптимизации хранения и обработки. Эти константы можно настраивать под ваши нужды
- **Конфигурация контекста**: По умолчанию, бот хранит контекст длиной 6500 символов и/или 15 сообщений (где запрос пользователя и ответ бота считаются одним сообщением).

## Структура базы данных

### Таблица: `users_chats`

| Поле            | Тип            | Описание                                   |
| --------------- | -------------- | ------------------------------------------ |
| `CHAT_ID`       | BIGINT         | Уникальный идентификатор чата              |
| `TEXT`          | TEXT           | Хранение текста беседы                     |
| `MESSAGE_COUNT` | DECIMAL(0, 15) | Количество сообщений в текущем контексте   |
| `USER_NAME`     | VARCHAR        | Имя пользователя                           |
| `VERSION`       | INT            | Версия записи (для управления изменениями) |

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
6. Настройте переменную окружения с токеном:
   ```bash
   export TOKEN_BOT=your_bot_token_telegram_here
   export TOKEN_OPENAI=your_bot_token_open_ai__here
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
```
