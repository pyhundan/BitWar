package analyze;

import error.CodeException;
import modle.Users;

import java.util.HashMap;
import java.util.LinkedList;

public class StateAnalyze {

    String fail;

    public LinkedList<Toke> tokes;
    public Toke tok;
    public int index;
    public HashMap<String,Integer> identifier;
    public int back;
    public boolean backflag;
    public History history;
    public int oppenent;

    public void setHistory(History history,int op){
        this.history=history;
        oppenent=op;
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
            Error("wrong word! expected："+a+" actually:"+tok.word+"index:"+(index-1)+";line"+tok.line);
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
            fail=e.message;
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
        else Error("stmt_seq not corresponding! (index :"+(index-1)+",line :"+tok.line+")");
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
        else Error("statement not corresponding! (index :"+(index-1)+",line :"+tok.line+")");
    }

    public void if_stmt() throws CodeException
    {
        match("if");
        match("(");
        int flag=exp();
        match(")");
       // match("then");
        match("{");
        if (flag==1)
        {

            stmt_seq();
        }
        else
        {

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
        else Error("back not corresponding! (index :"+(index-1)+",line :"+tok.line+")");

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
        else if (tok.type.equals("system")){
            if (tok.word.equals("RANDOM")){
                temp=random();
            }
            else if (tok.word.equals("LATEST")){
                if (history==null) {temp=-1;}
                else{ temp=history.result.size()-1;}
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
        if (history==null)
        {
            return 0;
        }
        else return (history.result.get(temp))[oppenent];

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


    public static void main(String args[]){

        Users users=new Users("随机1.txt");
        System.out.println(users.wordAnalyze.tokes);
        users.stateAnalyze.start_analyse();

    }
}
