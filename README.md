# Неизменяемые структуры данных (Persistent Data Structures)
## Линевич Дмитрий 21222
## Щеголева Ангелина 21221
## Цель: реализовать неизменяемые структуры данных на Java.
## Анализ алгоритмов: 
Неизменяемые структуры данных сохраняют свои предыдущие версии при изменении и, следовательно, являются фактически неизменными. Полностью постоянные структуры данных допускают как обновления, так и запросы к любой версии.
Многие операции вносят лишь небольшие изменения. Поэтому просто копирование предыдущей версии было бы неэффективным. Чтобы сэкономить время и память, важно определить сходство между двумя версиями и переиспользовать как можно больший объем данных.
Чаще всего в литературе о реализации неизменяемых структур данных встречаются два алгоритма Fat node и Path copying, а также их комбинации и улучшения (https://en.m.wikipedia.org/wiki/Persistent_data_structure).
### Fat node
Суть алгоритма состоит в том, чтобы записывать все изменения, внесенные в поля узлов в самих узлах, без удаления старых значений полей. Главными проблемами данного алгоритма являются большой объем занимаемой памяти и амортизация времени для сохранения модификации из-за увеличения размеров узлов. 
### Path copying
При использовании данного алгоритма создаются копии каждого узла встреченного на пути к измененному узлу. Поэтому для каждого изменения будет создан новый корень, по сути являющийся “новой версией” структуры данных. 
### Комбинации:
Также существует множество комбинаций данных алгоритмов, например, описанные в данной статье https://www.sciencedirect.com/science/article/pii/0022000089900342. Здесь описывается комбинация алгоритмов с привязкой к частному случаю деревьев поиска. Главная идея - перемещаемое хранилище изменений. Вместо того, чтобы сохранять изменение в соответствующем постоянном узле, информация об изменении хранится в каком-то, возможно, другом узле, который лежит на пути к измененному узлу в новой версии. 
## Выбор реализации:
Реализация неизменяемых структур данных по алгоритму  Path copying с использованием B-дерева в качестве базовой структуры данных (https://en.m.wikipedia.org/wiki/B-tree).
## API:
Требуется для всех структур реализовать API, соответствующий существующим классам в Java.
### Массив
https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html 
### Двусвязный список
https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html 
### Ассоциативный массив
https://docs.oracle.com/javase/8/docs/api/java/util/Map.html 
## Дополнительные требования:
Обеспечить произвольную вложенность данных (по аналогии с динамическими языками), не отказываясь при этом полностью от типизации посредством generic/template.
Реализация с использованием generic и оператора instanceOf, который позволит реализовать особую логику для вложенных структур данных.
Реализовать универсальный undo-redo механизм для перечисленных структур с поддержкой каскадности (для вложенных структур).
Реализация сохранения истории изменений при помощи двух стеков операций: совершенных и отменных. 
Расширить экономичное использование памяти на операцию преобразования одной структуры к другой (например, списка в массив).
Path copying использует единую базовую структуру данных, что позволяет легко производить преобразование одной структуры к другой путем переиспользования базовой.