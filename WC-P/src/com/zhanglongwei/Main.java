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
        //����ͳ�Ʒ���
        m.input.close();
        //�رռ�����Դ
    }

    public void wordCount() {
        String command = "";
        while (true) {
            //ѭ����ָ����ʽ�˳�����
            System.out.println("���롰base��ʹ�û������ܣ����롰extension��ʹ����չ���ܣ����롰end����������");
            //��ʾ
            command = input.nextLine();
            if ("base".equals(command)) {
                System.out.println("�����ʽ���£�");
                System.out.println("wc.exe -c file.c     //�����ļ� file.c ���ַ���");
                System.out.println("wc.exe -w file.c    //�����ļ� file.c �ĵ��ʵ���Ŀ");
                System.out.println("wc.exe -l file.c      //�����ļ� file.c ������");
                baseShow(input.nextLine());
                //�����жϷ���
            } else if ("extension".equals(command)) {
                System.out.println("wc.exe -s directory .*     //�ݹ鴦��Ŀ¼�·��ϡ�.*���������ļ������ַ������ܵ��ʵ���Ŀ��������");
                System.out.println("wc.exe -a file.*        //���ظ����ӵ����ݣ������� / ���� / ע���У���");
                extensionShow(input.nextLine());
                //�����жϷ���
            } else if ("end".equals(command)) {
                break;
                //end��������
            } else {
                System.out.println("������������������");
            }
            //�ж��������
        }
    }

    public void baseShow(String command) {
        if (!command.contains(".c")) {
            System.out.println("�ļ���ʽ����*.c��");
            return;
            //�ж��Ƿ�Ϊ�������ܵ�ָ����ʽ
        }
        String path = command.split(" ")[2];
        //�õ�·��
        List<Integer> list = base(path);
        //�õ���������
        if (command.contains("wc.exe -c ")) {
            System.out.println("�ļ��ַ�����" + list.get(0));
        } else if (command.contains("wc.exe -w ")) {
            System.out.println("�ļ���������" + list.get(1));
        } else if (command.contains("wc.exe -l ")) {
            System.out.println("�ļ���������" + list.get(2));
        } else {
            System.out.println("�������");
        }
        //������ָ���������
    }

    public void extensionShow(String command) {
        if (!(command.contains("wc.exe -s ") || command.contains("wc.exe -a "))) {
            System.out.println("�������");
            return;
            //�ж������Ƿ���ȷ
        }
        String path = command.split(" ")[2];
        //�õ�·��
        String format = command.substring(command.lastIndexOf("."), command.length());
        //�õ�ָ������Ŀ¼���ļ��ĸ�ʽ
        if (command.contains("wc.exe -s ")) {
            List<File> fileList = extension(path, format);
            //�õ������ȷ�ļ�����
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
            //���������ܺ�
            System.out.println("Ŀ¼���ַ�����" + countList.get(0));
            System.out.println("Ŀ¼�µ�������" + countList.get(1));
            System.out.println("Ŀ¼����������" + countList.get(2));
            //���
        } else if (command.contains("wc.exe -a ")) {
            List<Integer> list = base(path);
            //�õ�ĳ���ļ��ĸ�������
            System.out.println("�ļ�����������" + list.get(5));
            System.out.println("�ļ���������" + list.get(3));
            System.out.println("�ļ�ע��������" + list.get(4));
        }
    }

    private List<Integer> base(String filePath) {
        boolean flag = false;//�Ƿ�������ע�͵ı�־
        List<Integer> countList = new ArrayList<>();
        String annotationBegin = "/*";//����ע����
        String annotationEnd = "*\\";//����ע��β
        String annotationSingle  = "//";//����ע��
        int charLength = 0;//�ַ���
        int wordSum = 0;//������
        int lineSum = 0;//������
        int blankLine = 0;//�հ�����
        int annotationLine = 0;//ע������
        int codeLine = 0;//��������
        FileReader fr = null;
        BufferedReader br = null;
        String tempString = "";
        StringBuffer sb = new StringBuffer();
        try {
            fr = new FileReader(filePath);
            //ʹ�û����ַ������а�װ
            br = new BufferedReader(fr);
            while ((tempString = br.readLine()) != null) {
                //br.readLine()�����ķ���ֵ��һ���ַ������󣬼��ı��е�һ�����ݣ����Զ�ÿһ�н����ж�
                lineSum ++;
                //ÿ��һ�У�����������
                charLength  += tempString.length();
                //�ַ���Ϊÿһ�е��ַ���֮��
                if (tempString.length() <= 1) {
                    blankLine ++;
                    continue;
                    //�ж��Ƿ�հ��У��ǵĻ�����������ж��Ƿ�ע���е�
                }
                if (tempString.contains(annotationBegin) && !tempString.contains(annotationEnd)) {
                    annotationLine++;
                    flag = true;
                    //�ж��Ƿ�Ϊ����ע�ͣ�Ϊ����ע����Ϊ����ע��β
                } else if (flag) {
                    annotationLine ++;
                    //�������ע�ͣ�ע��������
                } else if (tempString.contains(annotationEnd) ) {
                    annotationLine ++;
                    flag = false;
                    //�ж�Ϊ����ע�ͽ�β������ע�ͽ���
                } else if (tempString.contains(annotationSingle)) {
                    annotationLine ++;
                    //�ж�Ϊ����ע��
                }
                sb.append(tempString);
                //���ÿһ��ע�͵�StringBuffer����
                charLength  += tempString.length();
                //�ַ���Ϊÿһ�е��ַ���֮��
            }
            wordSum = pattern(sb);
            //ͳ�Ƶ�����
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
        //�ر����Ȳ���
        codeLine = lineSum - blankLine -annotationLine;
        //���������
        countList.add(charLength);
        countList.add(wordSum);
        countList.add(lineSum);
        countList.add(blankLine);
        countList.add(annotationLine);
        countList.add(codeLine);
        //������ݵ�List<Integer> ������
        return countList;
        //����
    }

    private  int pattern(StringBuffer sb) {
        int sum = 0;
        Pattern pattern = Pattern.compile("[a-zA-Z']+");
        Matcher matcher = pattern.matcher(sb);
        while(matcher.find()) {
            sum++;
        }
        return sum;
        //ͳ�Ƶ���
    }

    private List<File> extension(String sourcePath, String format) {
        List<File> fileList = new ArrayList<>();
        //�ļ�list����
        File targetFile  = new File(sourcePath);
        //�ļ�����
        if (targetFile.exists()) {
            File[] files = targetFile.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("Ŀ¼Ϊ��");
                return null;
                //�п�
            } else {
                for (File tempFile : files) {
                    if (tempFile.isDirectory()) {
                        extension(tempFile.getAbsolutePath(), format);
                        //��Ŀ¼��ݹ����
                    } else if (tempFile.getName().contains(format)) {
                        fileList.add(tempFile);
                        //��ָ����ʽ����ӵ��ļ��б�
                    }
                }
            }
        }else {
            System.out.println("Ŀ¼������!");
        }
        return fileList;
        //�����ļ��б�
    }

}
