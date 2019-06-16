package analyze;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

class Toke {
    //public static int no=0;

    int number;
    String word;
    String type;
    int line;

    public Toke(int n, int l)
    {
        this.number=n;
        this.line=l;
    }

    @Override
    public String toString() {
        return number+"+"+word+"+"+type+'\n';
    }
}
//词法分析器
public class WordAnalyze {

    public LinkedList<Toke> tokes;
    public String source;

    int no;
    int line;

    final static int LETTER=0;
    final static int NUMBER=1;
    final static int CHAR=2;
    final static int SYSTEM=3;
    final static int START=-1;

    public static HashSet<String> reserer=new HashSet<>();


    public WordAnalyze(String s){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader("test.txt"));
            String temp=bufferedReader.readLine();
            while(temp!=null)
            {
                reserer.add(temp.trim());
                temp=bufferedReader.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        source=s;
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

        while(index<source.length())
        {
            char cur=source.charAt(index);
            if (state==START)
            {
                state=getWhat(cur);
            }
            switch (state)
            {
                case LETTER:
                    if (Character.isAlphabetic(cur))
                    {
                        temp=temp+cur;
                        cur=source.charAt(index++);
                        continue;
                    }
                    else{
                        Toke t=new Toke(no++,line);
                        t.word=temp;
                        if (reserer.contains(temp))
                        {
                            t.type="reserve";
                        }
                        else if (temp.equals("RANDOM")||temp.equals("HISTORY")||temp.equals("LATEST"))
                        {
                            t.type="system";
                        }
                        else t.type="identifier";
                        tokes.addLast(t);
                        temp="";
                        state=START;
                    }
                    break;
                case NUMBER:
                    if (cur>='0'&&cur<='9')
                    {
                        temp=temp+cur;
                        cur=source.charAt(index++);
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
