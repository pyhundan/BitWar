package analyze;

import error.CodeException;

import java.util.HashMap;
import java.util.LinkedList;

public class StateAnalyze {

    String fail_message;

    public Toke tok;
    public LinkedList<Toke> tokes;
    public int index;
    public HashMap<String,Integer> identifier;
    public int back;//返回值
    public boolean backflag;
    public Array array;
    public int under;
    public void setArray(Array array, int op){
        this.array = array;
        under =op;
    }
    //构建标识符列表
    public StateAnalyze(LinkedList<Toke> list)
    {
        this.tokes =list;
        identifier=new HashMap<>();
        for(int i = 0; i< tokes.size(); i++)
        {
            Toke temp= tokes.get(i);
            if(temp.type.equals("identifier"))
            {
                identifier.put(temp.word,0);
            }
        }
    }
    //递归下降分析方法
    //随机数
    public int random() throws CodeException
    {
       match("RANDOM");
       match("(");
       int a=simple_exp()+1;
       int temp=(int)(Math.random()*a);
       match(")");
       return temp;
    }
    //错误提示
    public void Error(String a) throws CodeException
    {
        System.err.print(a);
        throw new CodeException(a);
    }
    //匹配函数
    public void match(String a) throws CodeException
    {
        if(tok.word.equals(a)||tok.type.equals(a))
        {
            if (index< tokes.size())
            {
                tok= tokes.get(index++);
            }
        }
        else{
            Error("wrong word! need："+a+" but word is:"+tok.word+"index:"+(index-1)+";line"+tok.line);
        }
    }
    public void start_analyse()
    {
        index=0;
        backflag=true;
        back=0;
        try {
            tok= tokes.get(index++);
            stmt_seq();
        }catch (CodeException e)
        {
            fail_message =e.message;
        }
    }
    public void stmt_seq() throws CodeException
    {
        String word=tok.word;
        if(word.equals("if")||word.equals("repeat")||word.equals("back")||tok.type.equals("identifier"))
        {
            statement();
            while (tok.word.equals(";"))
            {
                match(";");
                statement();
            }
        }
        else Error("stmt_seq error! (index :"+(index-1)+",line :"+tok.line+")");
    }
    public void statement() throws CodeException
    {
        String word=tok.word;
        if (word.equals("if"))
        {
            if_stmt();
        }
        else if (word.equals("repeat"))
        {
            repeat_stmt();
        }
        else if(word.equals("back"))
        {
            back_stmt();
        }
        else if (tok.type.equals("identifier"))
        {
            assign_stmt();
        }
        else Error("statement error! (index :"+(index-1)+",line :"+tok.line+")");
    }
    public void if_stmt() throws CodeException
    {
        match("if");
        match("(");
        int flag=exp();
        match(")");
       // match("then");
        match("{");
        if (flag==1) {
            stmt_seq();
        }
        else {
            Toke t=tok;
            //System.out.println(t.word);
            int i=index;
            while(!t.word.equals("}"))
            {
                if (t.word.equals("{")) {
                    //match("{");
                }
                t= tokes.get(i++);
            }
            tok=t;
            index=i;
        }
        match("}");
        if (tok.word.equals("else")){
            match("else");
            match("{");
            if (flag==1)
            {
                index=findright(index-1);
                tok= tokes.get(index++);
            }
            else{
                stmt_seq();
            }
            match("}");
        }
        match("end");
    }
    public void repeat_stmt() throws CodeException
    {
        match("repeat");
        match("(");
        int exp_index=index-1;
        int temp=exp();
        match(")");
        match("{");
        int spin_index=index-1;
        int spined_index=findright(index-1);
        while (temp==1){
            stmt_seq();
            index=exp_index;
            tok= tokes.get(index++);
            temp=exp();
            index=spin_index;
            tok= tokes.get(index++);
        }
        index=spined_index;
        tok= tokes.get(index++);
        match("}");
        match("endrepeat");
    }
    public void assign_stmt() throws CodeException
    {
        String temp=tok.word;
        match("identifier");
        match(":");
        match("=");
        int temp1=simple_exp();
        identifier.put(temp,temp1);
    }
    public void back_stmt() throws CodeException
    {
        match("back");
        if (tok.type.equals("identifier"))
        {
            if (backflag)
            {
                back=identifier.get(tok.word);
                backflag=false;
            }
            match("identifier");
        }
        else if (tok.type.equals("number"))
        {
            if (backflag)
            {
                back=Integer.parseInt(tok.word);
                backflag=false;
            }
            match("number");
        }
        else Error("back error! (index :"+(index-1)+",line :"+tok.line+")");

    }
    public int exp() throws CodeException
    {
        int temp=simple_exp();
        while(tok.word.equals("=")||tok.word.equals("<")||tok.word.equals(">"))
        {
            char compare=tok.word.charAt(0);
            match("character");
            int temp1=simple_exp();
            switch (compare)
            {
                case '=':
                    if(temp==temp1) temp=1;
                    else temp=0;
                    break;
                case '>':
                    if (temp>temp1) temp=1;
                    else temp=0;
                    break;
                case '<':
                    if (temp<temp1) temp=1;
                    else temp=0;
                    break;
            }
        }
        return temp;
    }
    public int simple_exp() throws CodeException
    {
        int temp=term();
        while(tok.word.equals("-")||tok.word.equals("+"))
        {
            char c=tok.word.charAt(0);
            match("character");
            switch (c)
            {
                case '-':temp=temp-term();break;
                case '+':temp=temp+term();break;
            }
        }
        return temp;

    }
    public int term() throws CodeException
    {
        int temp=factor();
        while(tok.word.equals("*")||tok.word.equals("/"))
        {
            char c=tok.word.charAt(0);
            match("character");
            switch (c)
            {
                case '*':temp=temp*term();break;
                case '/':temp=temp/term();break;
            }
        }
        return temp;
    }
    public int factor() throws CodeException
    {
        int temp=0;
        if (tok.word.equals("("))
        {
            match("(");
            temp=simple_exp();
            match(")");
        }
        else if (tok.type.equals("number"))
        {
            temp=Integer.parseInt(tok.word);
            match("number");
        }
        else if (tok.type.equals("identifier"))
        {
            temp=identifier.get(tok.word);
            match("identifier");
        }
        else if (tok.type.equals("mine")){
            if (tok.word.equals("RANDOM")){
                temp=random();
            }
            else if (tok.word.equals("LATEST")){
                if (array ==null) {temp=-1;}
                else{ temp= array.result.size()-1;}
                System.out.println("LATEST="+temp);
                match("LATEST");
            }
            else {
                temp=HISTORY_CALL();
            }
        }
        else Error("not a factor! (index:"+(index-1)+",line:"+tok.line+")"+tok.word);
        return temp;
    }
    public int HISTORY_CALL() throws CodeException
    {
        match("HISTORY");
        match("[");
        int temp=simple_exp();
        match("]");
        if (array ==null)
        {
            return 0;
        }
        else return (array.result.get(temp))[under];

    }
    private int findright(int curindex){
        int temp = curindex;
        Toke t = tokes.get(curindex);
        while(!t.word.equals("}")){
            if(t.word.equals("{")){
                temp = findright(temp+1);
            }
            t = tokes.get(++temp);
        }
        return temp;
    }
//测试
//    public static void main(String args[]){
//
//        Users users=new Users("随机1.txt");
//        System.out.println(users.wordAnalyze.tokes);
//        users.stateAnalyze.start_analyse();
//
//    }

//    文法规则：
//    stmt_seq -> statement { ; statement }
//    statement -> if_stmt|repeat_stmt|assign_stmt |back_stmt
//    if_stmt -> if(exp)then '{' stmt_seq'}' {else '{'stmt_seq'}'} end
//    repeat_stmt -> repeat(exp) '{'stmt_seq'}' endrepeat
//    assign_stmt -> identifier:=exp
//    back_stmt -> back(identifier|number)
//    exp -> simple_exp {comparison_op simple_exp}
//    comparison_op -> <|=|>
//    simple_exp -> term { addop term}
//    addop -> +|-
//    term -> factor {mulop factor}
//    mulop -> *|/
//    factor -> (exp)|number|identifier|RANDOM|HISTORY_CALL
//    HISTORY_CALL -> HISTORY[simple_exp]
//    RANDOM -> (random)




}
