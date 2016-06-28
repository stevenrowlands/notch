package rowley.eclipse.notch.bindings.java;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;

import groovy.lang.Closure;

public interface JavaManipulation {

	/**
	 * Adds an import to a java class
	 * @param import the fully qualified class name to add as an import
	 * @return the eclipse import declaration that was created
	 */
	public IImportDeclaration addImport(String contents);
	
	/**
	 * Adds a constructor to a java class. The constructor is automatically added
	 * at the top of the class after any fields and before any methods are defined
	 * 
	 * @param constructor the full java code that defines the constructor
	 * @return the eclipse method that was created
	 */
	public IMethod addConstructor(String contents);
	
	
	/**
	 * Adds a method to a java class. The method is automatically added
	 * at the bottom of the class
	 * 
	 * @param constructor the full java code that defines the method
	 * @return the eclipse method that was created
	 */
	public IMethod addMethod(String contents);
	

	/**
	 * Adds a method to a java class. The method is automatically added
	 * at the bottom of the class
	 * 
	 * @param constructor the full java code that defines the method
	 * @return the eclipse method that was created
	 */
	public IField addFieldFirst(String contents);
	
	
	
	/**
	 * Adds a method to a java class. The method is automatically added
	 * at the bottom of the class
	 * 
	 * @param constructor the full java code that defines the method
	 * @return the eclipse method that was created
	 */
	public IField addFieldLast(String contents);
	
	/**
	 * Creates a template and runs the closure at completion.
	 * 
	 * @param template the eclipse template
	 * @param closure the closure to run after the template is finished
	 */
	public void template(String template, Closure closure);

	
	/**
	 * Shifts focus to a given java member
	 * @param type the member to focus. The entire member will be selected
	 */
	public void focus(IMember type);
	
	/**
	 * Shifts focus to before a given java member
	 * @param type the member to focus before
	 */
	public void focusBefore(IMember type);
	
	/**
	 * Shifts focus to after a given java member
	 * @param type the member to focus after
	 */
	public void focusAfter(IMember type);
	
	/**
	 * @return the java project for the current editor
	 */
	public IJavaProject getProject();
}
