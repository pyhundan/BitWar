package analyze;

import modle.Users;

import java.util.LinkedList;

public class Array {

    //S1，S2的得分策略
    //  1  5    1  0
    //  0  3    5  3
    public static int s1[][]=new int [][]{{1,5},{0,3}};
    public static int s2[][]=new int [][]{{1,0},{5,3}};
    public static int no=0;

    int number;
    Users ss1,ss2;

    public LinkedList<Integer[]> result;
    int s1_sum=0,s2_sum=0;
    int s1_time=0,s2_time=0;

    public Array(Users users1, Users users2)
    {
        ss1=users1;
        ss2=users2;
        result=new LinkedList<>();
        number=no++;
    }


}
