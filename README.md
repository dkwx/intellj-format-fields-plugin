# intellj-format-fields-plugin
 这个插件可以将Java文件的全局字段排序，按代码的长度由短到长排序。
        不选中内容，默认格式化整个文件，这时idea会自动根据属性的修饰符分组，很遗憾，目前无法改变。
        选中内容，只会格式化选中内容，而且完全按长度排序，不分组，建议选中内容格式化。
        
 This plug-in can sort the global fields of Java files, from short to long, according to the length of the code.
        Unselected, the entire file is formatted by default, and idea is automatically grouped by the modifier of the property, which unfortunately cannot be changed at this time.
        Selected content, will only format selected content, and completely sorted by length, no grouping, it is recommended that selected content format.

        # 格式化例子见下:
        For example:
        int i;
        @Resource
        Service service;
        boolean bool;

       # After handle:
        int i;
        boolean bool;
        @Resource
        Service service;
