# yta


Ссылка на APK: https://yadi.sk/d/Vqt5vwY93Gzfg8

*При развертывании проекта необходимо указать свой путь к sdk в файле local.properties (создать файл в корне проекта, если его нет)* 

## Описание работы приложения:

Приложение состоит из одного активити, содержащего три фрагмента: FragmentAction (Перевод), FragmentHistory (История), FragmentFavorite (Избранное).

Процесс получения данных: 
Для работы с API Яндекса используется Retrofit.
Кеширование осуществляется с помощью одной из таблиц локальной базы данных - CacheModel (Используется GreenDAO ORM).
В приложении описан класс ServiceGenerator, который генерирует разные экземпляры Retrofit (с помощью фабричных методов) для доступа к Api переводчика и словаря. В этом же классе описана логика отправки запроса:
- 1 Проверяется состояние сети 
	- 1.1 Отсутствует подключение к сети
		- 1.1.1 Производится поиск в кеше и возвращается значение
		- 1.1.2 В кеше отстутствет информация -> возвращается соответствующий Exception 
	- 1.2 Присутсвтует подключение к сети - переход к п. 2
- 2 В кешу производится поиск записи по соответствующей "сигнатуре" (в данном случае это url + параметры запроса)
	- 2.1 Запись была найдена - возвращается объект из кеша
	- 2.2 Запись не найдена, переход к п.3
- 3 Выполняется POST-запрос
	- 3.1 Запрос выполнен успешно (код 200) -> ответ сохраняется в кеше, переход к п.4
	- 3.2 Запрос возвращает ошибку (код, отличный от 200) -> возвращается соответствующей Exception
- 4 Возвращается результат запроса

*В таблице CacheModel ответ от сервера хранится в виде строки, представленной в JSON формате (такая строка впоследствии может конвертироваться в объект с помощью GSON)

**За процесс отправки запросов отвечает объект класса ApiChainRequestWrapper, см. описание в коде (кратко - инкапсулирует запросы и выполняет их поочередно, сохраняя результат каждого)

Описание остальных элементов программы представлено в комментариях к коду программы.

Структура и описание пакетов:
app/java/ru.belokonalexander.yta/...
- 1 Adapters - адаптеры для списков, использующихся в приложении
- 2 Database - пакет с сущностями, описывающими таблицы локальной базы данных (используется ORM). Search-классы, представленные в данном пакете - дополнительный функционал, для создания фильтра для определенных полей объектов (см. SearchRecyclerView)
- 3 Events - события для EventBus
- 4 GlobalShell - пакет для вспомогательных классов: настройки и т.д
	- 4.1 Models - GSON конвертирует ответы сервера в объекты, описанные в данном пакете
-5 Views - представления и связанные с ними классы

