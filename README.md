# tba: Text Based Adventure

A text-based game for text-based platforms!

First create a bot through telegrams "Botfather" and its token to `TELEGRAM_BOT_TOKEN`
From `tba.core` in the repl, start a bot with:

`(start "tba" (System/getenv "TELEGRAM_BOT_TOKEN") handlers/router)`
