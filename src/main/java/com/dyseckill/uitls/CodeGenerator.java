package com.dyseckill.uitls;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/dyseckill", "root", "root")
                .globalConfig(builder -> {
                    // 设置作者
                    builder.author("dysjsjy")
                            // 指定输出目录为项目的src/main/java目录
                            .outputDir("/Users/ningjiang/IdeaProjects/dyseckill/src/main/java");
                })
                .packageConfig(builder -> {
                    // 设置父包名，与项目包名一致
                    builder.parent("com.dyseckill")
                            // 设置模块名，可根据实际情况修改
                            .moduleName("")
                            // 设置mapperXml生成路径到项目的resources/mapper目录
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "/Users/ningjiang/IdeaProjects/dyseckill/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    // 移除addInclude方法，让代码生成器处理所有表
                    // 设置过滤表前缀
                    builder.addTablePrefix("t_", "c_");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}