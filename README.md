# TeleBot

TeleBot is a Telegram bot that lets you download youtube videos.


## Youtube-dl's examples
<https://github.com/rg3/youtube-dl/blob/master/README.md#readme>

youtube-dl:
* Получить имя файла: `youtube-dl --get-filename -f mp4/best https://m.youtube.com/watch\?v\=fOaTUQqj2m0`
* Запустить в симуляционном режиме: `youtube-dl -s -f mp4/best https://m.youtube.com/watch\?v\=fOaTUQqj2m0`


## Запуск shell-комманды
(use '[clojure.java.shell :only [sh]])

`(sh "resources/sleep.sh")`
`(sh "youtube-dl" "--no-progress -s -f mp4/best https://m.youtube.com/watch?v=fOaTUQqj2m0")`

## Переменные окружения
- Берутся из profiles.clj и заносятся в .lein-env (это если установлен plugin environ)
- `lein with-profile +prod repl` можно запустить специальное окружение

## Usage

You'll need to get your own [Telegram API key](https://core.telegram.org/bots#3-how-do-i-create-a-bot).

ClojureBot uses [Environ](https://github.com/weavejester/environ), so you should set your `api-key` environment variable to your own key.

If you are running the bot using [leiningen](http://leiningen.org), you can add your key to your `profiles.clj` file, like so:

```clojure
{:dev
 {:env {:api-key "your-api-key"}}}
```

Or in:
`.lein-env`
```
{:api-key "your-api-key"}
```

Or in bash:
`export API_KEY=your-api-key`

TeleBot is a fork ClojureBot.

## License

Copyright © 2018 Denis Larionov.

The use and distributon for this software are covered by the [Eclipse Public License 1.0](https://www.eclipse.org/legal/epl-v10.html).

See also [LICENSE](./LICENSE)

TeleBot includes code from [clj-slackbot](https://github.com/verma/clj-slackbot), which is
Copyright © 2014 Uday Verma. Licensed under the same terms as Clojure (EPL).
