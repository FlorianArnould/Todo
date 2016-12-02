package com.socket.florian.todo.storage;



public class Duration{

    private int _years;
    private int _days;

    public Duration(int years, int days){
        _years = years;
        _days = days;
    }

    public String toString(){
        String str = "";
        if(_years == 0 && _days == 0){
            return "illimité";
        }
        if(_years == 1){
            str += String.valueOf(_years) + " année ";
        }else if(_years > 1){
            str += String.valueOf(_years) + " années ";
        }
        if(_days == 1){
            str += String.valueOf(_days) + " jour ";
        }else if(_days > 1){
            str += String.valueOf(_days) + " jours ";
        }
        return str;
    }
}
