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
public class EntityManagerException extends Exception {

    /**
     * Creates a new instance of <code>EntityManagerException</code> without
     * detail message.
     */
    public EntityManagerException() {
    }

    /**
     * Constructs an instance of <code>EntityManagerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public EntityManagerException(String msg) {
        super(msg);
    }
}
