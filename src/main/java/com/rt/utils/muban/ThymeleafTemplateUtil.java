package com.rt.utils.muban;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

public class ThymeleafTemplateUtil {
    //项目中必须有resources目录下面有templates目录，Html页面写在templates下
    public static final String TEMPLATES_PATH = "templates/";

    public static final String TEMPLATES_SUFFIX = ".html";

    TemplateEngine templateEngine = new TemplateEngine();

    private static ThymeleafTemplateUtil util = new ThymeleafTemplateUtil();

    public ThymeleafTemplateUtil() {

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        // 构造模板引擎
        resolver.setPrefix(TEMPLATES_PATH);// 模板所在目录，相对于当前classloader的classpath。
        resolver.setSuffix(TEMPLATES_SUFFIX);// 模板文件后缀
        resolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(resolver);
    }

    public String parseThymeleafTemplate(String templateName, Map<String, Object> contextMap) {

        Context context = new Context();
        if (contextMap != null) {
            // 构造上下文(Model)
            contextMap.forEach(context::setVariable);
        }

        // 渲染模板

        return templateEngine.process(templateName, context);
    }

    /**
     * 提供外部的静态方法 根据模板名称和上下文Map生成模板文件内容
     *
     * @param templateName
     *            模板名称 templates/ 下的，无需后缀
     * @param contextMap
     *            上下文参数
     * @return
     */
    public static String parseTemplate(String templateName, Map<String, Object> contextMap) {
        return util.parseThymeleafTemplate(templateName, contextMap);
    }

    public static String parseTemplate(String templateName, Context context) {
        return util.templateEngine.process(templateName, context);
    }
}
