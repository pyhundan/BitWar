package analyze;


import java.util.LinkedList;

class Token{
    //public static int no=0;

    int number;
    String word;
    String type;
    int line;

    public Token(int n,int l)
    {
        this.number=n;
        this.line=l;
    }

    @Override
    public String toString() {
        return number+"+"+word+"+"+type+'\n';
    }
}
public class WordAnalyze {

    public LinkedList<Token> tokens;



}
