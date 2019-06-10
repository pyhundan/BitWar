import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

class Token{
    public static int no = 0;

    int number;
    String word;
    String type;
    int line;

    /*public Token(){
        number = no++;
    }*/
    public Token(int n,int l){number = n;line = l;}
    @Override
    public String toString() {
        return number + ":" + word + " " + type + '\n';
    }
}

/**
 * 执行词法分析的类
 */
public class WordAnalysis {
    public LinkedList<Token> tokens;
    public String source;
    int no;
    int line;

    final static int LETTER = 0;
    final static int NUMBER = 1;
    final static int CHAR = 2;
    final static int SYSTEM = 3;
    final static int START = -1;

    public static HashSet<String> reserver = new HashSet<>();

    /**
     * 传入程序源文件构造，读取本地存储的保留字
     * @param s
     */

    public WordAnalysis(String s){
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader("reserve.txt"));
            String line = bufferedReader.readLine();
            while(line != null){
                reserver.add(line.trim());
                line = bufferedReader.readLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        source = s;
        tokens = new LinkedList<>();
        no=0;
        line=1;
    }

    private int getStatus(char c){
        if(Character.isAlphabetic(c))
            return LETTER;
        else if(c <= '9' && c >= '0')
            return NUMBER;
        else return CHAR;
    }

    /**
     * 词法分析
     * @return
     */
    public List<Token> analysis(){
        int index = 0;
        int status=0;
        String tem="";
        while(index < source.length()){
            char cur = source.charAt(index);
            if(status == START){
                status = getStatus(cur);
            }
            switch (status){
                case LETTER:
                    if(Character.isAlphabetic(cur)){
                        tem += cur;
                        cur = source.charAt(index++);
                        continue;
                    }
                    else {
                        Token t = new Token(no++,line);
                        t.word = tem;
                        if(reserver.contains(tem)){
                            t.type = "reserve";
                        }
                        else if(tem.equals("RANDOM") || tem.equals("HISTORY") || tem.equals("LATEST")){
                            t.type = "system";
                        }
                        else t.type = "identifier";
                        tokens.addLast(t);
                        tem="";
                        status = START;
                    }
                    break;
                case NUMBER:
                    if(cur >= '0' && cur <= '9'){
                        tem += cur;
                        cur = source.charAt(index++);
                        continue;
                    }
                    else {
                        Token t = new Token(no++,line);
                        t.word = tem;
                        t.type = "number";
                        tokens.addLast(t);
                        tem="";
                        status = START;
                    }
                    break;
                case CHAR:
                    switch (cur){
                        case '\n' :
                            line++;
                            index++;
                            status=START;break;
                        case '+':
                        case '-':
                        case '=':
                        case '<':
                        case '>':
                        case '*':
                        case '/':
                        case ':':
                        case '.':
                        case '(':
                        case ')':
                        case '}':
                        case '{':
                        case '[':
                        case ']':
                            Token t = new Token(no++,line);
                            tem += cur;
                            t.type="character";
                            t.word = tem;
                            tokens.addLast(t);
                            status = START;
                            tem="";
                            index++;
                            break;
                        default:
                            index++;
                            status=START;
                            break;
                    }
                    break;
            }
        }
        return tokens;
    }

    /* 测试用
    public static void main(String args[]){
        StringBuffer all=new StringBuffer();
        try{
            BufferedReader reader = new BufferedReader(new FileReader("test.txt"));
            String line = reader.readLine();
            while(line != null){
                all.append(line);
                all.append('\n');
                line = reader.readLine();
            }
            WordAnalysis w = new WordAnalysis(all.toString());
            w.analysis();
            System.out.print(w.source);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(w.tokens);
        }
        catch (Exception e){

        }

    }*/
}
