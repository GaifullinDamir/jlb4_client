package Exceptions;

public class IncorrectPortException extends Exception{
    public String toString(){
        return "The port name must not exceed 4 characters.";
    }
}
