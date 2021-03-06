package com.zhanglongwei;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        Main m = new Main();
        m.wordCount();
        //调用统计方法
        m.input.close();
        //关闭键盘资源
    }

    public void wordCount() {
        String command = "";
        while (true) {
            //循环，指定格式退出程序
            System.out.println("输入“base”使用基础功能，输入“extension”使用扩展功能，输入“end”结束程序：");
            //提示
            command = input.nextLine();
            if ("base".equals(command)) {
                System.out.println("命令格式如下：");
                System.out.println("wc.exe -c file.c     //返回文件 file.c 的字符数");
                System.out.println("wc.exe -w file.c    //返回文件 file.c 的单词的数目");
                System.out.println("wc.exe -l file.c      //返回文件 file.c 的行数");
                baseShow(input.nextLine());
                //调用判断方法
            } else if ("extension".equals(command)) {
                System.out.println("wc.exe -s directory .*     //递归处理目录下符合“.*”条件的文件的总字符数、总单词的数目、总行数");
                System.out.println("wc.exe -a file.*        //返回更复杂的数据（代码行 / 空行 / 注释行）。");
                extensionShow(input.nextLine());
                //调用判断方法
            } else if ("end".equals(command)) {
                break;
                //end结束程序
            } else {
                System.out.println("输入有误，请重新输入");
            }
            //判断输入进行
        }
    }

    public void baseShow(String command) {
        if (!command.contains(".c")) {
            System.out.println("文件格式错误（*.c）");
            return;
            //判断是否为基础功能的指定格式
        }
        String path = command.split(" ")[2];
        //得到路径
        List<Integer> list = base(path);
        //得到各种数据
        if (command.contains("wc.exe -c ")) {
            System.out.println("文件字符数：" + list.get(0));
        } else if (command.contains("wc.exe -w ")) {
            System.out.println("文件单词数：" + list.get(1));
        } else if (command.contains("wc.exe -l ")) {
            System.out.println("文件总行数：" + list.get(2));
        } else {
            System.out.println("命令错误");
        }
        //由命令指定输出数据
    }

    public void extensionShow(String command) {
        if (!(command.contains("wc.exe -s ") || command.contains("wc.exe -a "))) {
            System.out.println("命令错误");
            return;
            //判断命令是否正确
        }
        String path = command.split(" ")[2];
        //得到路径
        String format = command.substring(command.lastIndexOf("."), command.length());
        //得到指定遍历目录中文件的格式
        if (command.contains("wc.exe -s ")) {
            List<File> fileList = extension(path, format);
            //得到多个正确文件对象
            List<Integer> countList = new ArrayList<>(3);
            countList.add(0);
            countList.add(0);
            countList.add(0);
            for (int i = 0; i < fileList.size(); i++) {
                List<Integer> tempList = base(fileList.get(i).getAbsolutePath());
                countList.set(0, countList.get(0) + tempList.get(0));
                countList.set(1, countList.get(1) + tempList.get(1));
                countList.set(2, countList.get(2) + tempList.get(2));
            }
            //遍历计算总和
            System.out.println("目录下字符数：" + countList.get(0));
            System.out.println("目录下单词数：" + countList.get(1));
            System.out.println("目录下总行数：" + countList.get(2));
            //输出
        } else if (command.contains("wc.exe -a ")) {
            List<Integer> list = base(path);
            //得到某个文件的各种数据
            System.out.println("文件代码行数：" + list.get(5));
            System.out.println("文件空行数：" + list.get(3));
            System.out.println("文件注释行数：" + list.get(4));
        }
    }

    private List<Integer> base(String filePath) {
        boolean flag = false;//是否进入多行注释的标志
        List<Integer> countList = new ArrayList<>();
        String annotationBegin = "/*";//多行注释起
        String annotationEnd = "*\\";//多行注释尾
        String annotationSingle  = "//";//单行注释
        int charLength = 0;//字符数
        int wordSum = 0;//单词数
        int lineSum = 0;//总行数
        int blankLine = 0;//空白行数
        int annotationLine = 0;//注释行数
        int codeLine = 0;//代码行数
        FileReader fr = null;
        BufferedReader br = null;
        String tempString = "";
        StringBuffer sb = new StringBuffer();
        try {
            fr = new FileReader(filePath);
            //使用缓冲字符流进行包装
            br = new BufferedReader(fr);
            while ((tempString = br.readLine()) != null) {
                //br.readLine()方法的返回值是一个字符串对象，即文本中的一行内容，所以对每一行进行判断
                lineSum ++;
                //每读一行，总行数增加
                charLength  += tempString.length();
                //字符数为每一行的字符数之和
                if (tempString.length() <= 1) {
                    blankLine ++;
                    continue;
                    //判断是否空白行，是的话跳过下面的判断是否注释行等
                }
                if (tempString.contains(annotationBegin) && !tempString.contains(annotationEnd)) {
                    annotationLine++;
                    flag = true;
                    //判断是否为多行注释，为多行注释起不为多行注释尾
                } else if (flag) {
                    annotationLine ++;
                    //进入多行注释，注释行增加
                } else if (tempString.contains(annotationEnd) ) {
                    annotationLine ++;
                    flag = false;
                    //判断为多行注释结尾，多行注释结束
                } else if (tempString.contains(annotationSingle)) {
                    annotationLine ++;
                    //判断为单行注释
                }
                sb.append(tempString);
                //添加每一行注释到StringBuffer对象
                charLength  += tempString.length();
                //字符数为每一行的字符数之和
            }
            wordSum = pattern(sb);
            //统计单词数
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //关闭流等操作
        codeLine = lineSum - blankLine -annotationLine;
        //计算代码行
        countList.add(charLength);
        countList.add(wordSum);
        countList.add(lineSum);
        countList.add(blankLine);
        countList.add(annotationLine);
        countList.add(codeLine);
        //添加数据到List<Integer> 对象中
        return countList;
        //返回
    }

    private  int pattern(StringBuffer sb) {
        int sum = 0;
        Pattern pattern = Pattern.compile("[a-zA-Z']+");
        Matcher matcher = pattern.matcher(sb);
        while(matcher.find()) {
            sum++;
        }
        return sum;
        //统计单词
    }

    private List<File> extension(String sourcePath, String format) {
        List<File> fileList = new ArrayList<>();
        //文件list对象
        File targetFile  = new File(sourcePath);
        //文件对象
        if (targetFile.exists()) {
            File[] files = targetFile.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("目录为空");
                return null;
                //判空
            } else {
                for (File tempFile : files) {
                    if (tempFile.isDirectory()) {
                        extension(tempFile.getAbsolutePath(), format);
                        //是目录则递归查找
                    } else if (tempFile.getName().contains(format)) {
                        fileList.add(tempFile);
                        //是指定格式则添加到文件列表
                    }
                }
            }
        }else {
            System.out.println("目录不存在!");
        }
        return fileList;
        //返回文件列表
    }

}
