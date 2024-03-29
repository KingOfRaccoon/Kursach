# GuApp

Кроссплатформенное приложение, разработанное под операционные системы Android, iOS, MacOs, Windows, Linux и для браузеров на технологии [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/), разрабатывалась как front-end курсовой работы.

## Описание проекта

Приложение разработано для просмотра расписания группы или преподавателя и возможности добавлять в сетку расписания своих мероприятий, чтобы все дела можно было видеть в одном месте.

## Стек приложения

- [Compose Multiplatfrom](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor](https://ktor.io/docs/welcome.html)
- [Kamel](https://github.com/Kamel-Media/Kamel)
- [Multiplafrom Settings](https://github.com/russhwolf/multiplatform-settings)
- [Koin](https://insert-koin.io)
- [Voyager](https://github.com/adrielcafe/voyager)

## Запуск приложения

IDE: Android Studio/Fleet (на macOS может запускать iOS) или связка Xcode (доступна только под macOS, нужна для запуска приложения на iOS) и IntellijIdea (нужна для запуска приложения под Android/Desktop/Web)

Плагины: необходимо установить плагин Kotlin Multiplatform Mobile
На macOS необходимо уставить cocoapods (это можно сделать через brew)

Запуск: для запуска мобильного приложения в эмуляторе понадобится виртуализация
Для Intel: Установка Intel HAXM
Для AMD: включить в настройках BIOS/UEFI виртуализацию

## Скриншоты
<p>
  <img src="../master/img2248.jpg" alt="Авторизация" width="300" />
  <img src="../master/img2254.jpg" alt="Регистрация" width="300" />
  <img src="../master/img2258.jpg" alt="Главный экран" width="300" />
  <img src="../master/img2262.jpg" alt="Главный экран (если на данный день нет дел и пар)" width="300" />
  <img src="../master/img2266.jpg" alt="Экран с детальной информацией о паре" width="300" />
  <img src="../master/img2270.jpg" alt="Экран с детальной информацией о личном мероприятии" width="300" />
  <img src="../master/img2274.jpg" alt="Экран поиска расписания" width="300" />
  <img src="../master/img2278.jpg" alt="Экран поиска расписания с найденным расписанием" width="300" />
  <img src="../master/img2282.jpg" alt="Экран полноразмерного календаря" width="300" />
  <img src="../master/img2288.jpg" alt="Экран с детальной информацией о паре в полноформатном календаре" width="300" />
  <img src="../master/img2292.jpg" alt="Экран создания нового мероприятия" width="300" />
  <img src="../master/img2296.jpg" alt="Экран создания нового тега мероприятий" width="300" />
</p>
