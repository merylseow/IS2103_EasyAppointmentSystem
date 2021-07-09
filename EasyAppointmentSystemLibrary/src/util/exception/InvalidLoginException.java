/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author jiajun
 */
public class InvalidLoginException extends Exception {

    /**
     * Creates a new instance of <code>InvalidLoginException</code> without
     * detail message.
     */
    public InvalidLoginException() {
    }

    /**
     * Constructs an instance of <code>InvalidLoginException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidLoginException(String msg) {
        super(msg);
    }
}
