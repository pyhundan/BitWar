package analyze;

import com.sun.tools.javac.jvm.Code;
import error.CodeException;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;


/*
    文法规则：

    stmt_seq -> statement { ; statement }
    statement -> if_stmt|repeat_stmt|assign_stmt |back_stmt
    if_stmt -> if(exp)then '{' stmt_seq'}' {else '{'stmt_seq'}'} end
    repeat_stmt -> repeat(exp) '{'stmt_seq'}' end repeat
    assign_stmt -> identifier:=exp
    back_stmt -> back(identifier|number) //返回语句
    exp -> simple_exp {comparison_op simple_exp}
    comparison_op -> <|=|>
    simple_exp -> term { addop term}
    addop -> +|-
    term -> factor {mulop factor}
    mulop -> *|/
    factor -> (exp)|number|identifier|RANDOM|HISTORY_CALL
    HISTORY_CALL -> HISTORY[simple_exp]
    RANDOM -> (random)

 */

public class StateAnalyze {

    String fail;

    public LinkedList<Token> tokens;
    public Token tok;
    public int index;
    public HashMap<String,Integer> identifier;
    public int back;
    public boolean backflag;
  //  public BattleHistory battleHistory
    //public int oppenent;
    //public void setBattleHistory(){};

    //构建标识符列表
    public StateAnalyze(LinkedList<Token> list)
    {
        this.tokens=list;
        identifier=new HashMap<>();
        for(int i=0;i<tokens.size();i++)
        {
            Token temp=tokens.get(i);
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
       int temp=(int)(Math.random());
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
            if (index<tokens.size())
            {
                tok=tokens.get(index++);
            }
        }
        else{
            Error("wrong word! expected "+a+"actual:"+tok.word+"index:"+(index-1)+";line"+tok.line);
        }
    }
    public void start_analyse()
    {
        index=0;
        backflag=true;
        back=0;

        try {
            tok=tokens.get(index++);
            stmt_seq();
        }catch (CodeException e)
        {
            fail=e.message;
        }
    }

    public void stmt_seq() throws CodeException
    {
        String word=tok.word;
        if(word.equals("if")||word.equals("repeat")||word.equals("back")||word.equals("identifier"))
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
        else if (word.equals("identifier"))
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
        match("{");
        if (flag==1)
        {
            stmt_seq();
        }
        else
        {
            Token t=tok;
            int i=index;
            while(t.word.equals("}"))
            {
                if (t.word.equals("{")) {
                    //match("{");
                }
                t=tokens.get(i++);
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
                tok=tokens.get(index++);
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
        match("for");
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
            tok=tokens.get(index++);
            temp=exp();
            index=spin_index;
            tok=tokens.get(index++);
        }
        index=spined_index;
        tok=tokens.get(index++);
        match("}");
        match("end");
        match("for");
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

        int temp=term();
        while(tok.word.equals("*")||tok.word.equals("/"))
        {
            char c=tok.word.charAt(0);
            switch (c)
            {
                case '*':temp=temp*term();break;
                case '?':temp=temp/term();break;
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
        else if (tok.word.equals("number"))
        {
            temp=Integer.parseInt(tok.word);
            match("number");
        }
        else if (tok.word.equals("identifier"))
        {
            temp=identifier.get(tok.word);
            match("identifier");
        }
        else if (tok.type.equals("system")){
            if (tok.word.equals("RANDOM")){
                temp=random();
            }
            else if (tok.word.equals("LATEST")){

                match("LATEST");
            }
        }
        else Error("not a factor! (index:"+(index-1)+",line:"+tok.line+")");
        return temp;
    }

    public void HISTORY_CALL() throws CodeException
    {

    }

    private int findright(int index)
    {
        int temp=index;
        Token t=tokens.get(index);
        while(!t.word.equals("}"))
        {
            if (t.word.equals("{"))
            {
                temp=findright(temp+1);
            }
            t=tokens.get(++temp);
        }
        return temp;
    }


}
