package analyze;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

//词法分析器
public class WordAnalyze {

    public LinkedList<Toke> tokes;
    public String save;

    int no;
    int line;

    final static int LETTER=0;
    final static int NUMBER=1;
    final static int CHAR=2;
    final static int START=-1;

    public static HashSet<String> reserver =new HashSet<>();
    //保存关键字

    public WordAnalyze(String s){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("test.txt"));
            String temp=bufferedReader.readLine();
            while(temp!=null)
            {
                reserver.add(temp.trim());
                temp=bufferedReader.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        save =s;
        tokes =new LinkedList<>();
        no=0;
        line=1;
    }

    public int getWhat(char c)
    {
        if(Character.isAlphabetic(c))
            return LETTER;
        else if(c<='9'&&c>='0')
            return NUMBER;
        else return CHAR;
    }

    public List<Toke> analyse()
    {
        int index=0;
        int state=0;
        String temp="";
        while(index< save.length())
        {
            char cur= save.charAt(index);
            if (state==START)
            {
                state=getWhat(cur);
            }
            switch (state)
            {
                case LETTER://扫描到字母
                    if (Character.isAlphabetic(cur))//继续扫描
                    {
                        temp=temp+cur;
                        cur= save.charAt(index++);
                        continue;
                    }
                    else{
                        Toke t=new Toke(no++,line);
                        t.word=temp;
                        if (reserver.contains(temp))
                        {
                            t.type="reserve";
                        }
                        else if (temp.equals("RANDOM")||temp.equals("HISTORY")||temp.equals("LATEST"))
                        {
                            t.type="mine";
                        }
                        else t.type="identifier";
                        tokes.addLast(t);
                        temp="";
                        state=START;
                    }
                    break;
                case NUMBER://扫描到数字
                    if (cur>='0'&&cur<='9')//继续扫描
                    {
                        temp=temp+cur;
                        cur= save.charAt(index++);
                        continue;
                    }
                    else{
                        Toke t=new Toke(no++,line);
                        t.word=temp;
                        t.type="number";
                        tokes.addLast(t);
                        temp="";
                        state=START;
                    }
                    break;
                case CHAR:
                    switch (cur)
                    {
                        case '\n':
                            line++;
                            index++;
                            state=START;
                            break;
                        case '+':
                        case '-':
                        case '=':
                        case '<':
                        case '>':
                        case '*':
                        case '/':
                        case ':':
                        case ';':
                        case '(':
                        case ')':
                        case '}':
                        case '{':
                        case '[':
                        case ']':
                            Toke t=new Toke(no++,line);
                            temp=temp+cur;
                            t.type="character";
                            t.word=temp;
                            tokes.addLast(t);
                            state=START;
                            temp="";
                            index++;
                            break;
                        default:
                            index++;
                            state=START;
                            break;
                    }
                    break;
            }
        }
        return tokes;
    }

}
