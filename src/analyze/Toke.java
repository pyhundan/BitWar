package analyze;

//用来保存单词以及类型
public class Toke {

    int number;
    String word;//单词
    String type;//类型
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