#notch

An eclipse plugin that allows for advanced commands and templates using groovy scripts.

All commands/templates can be registered to hotkeys allowing for faster access then normal eclipse templates (done via code completion)

see notch-scripts for some examples

##Java Examples

The following example shows how to add a private field and after generation automatically add (at the end of the class) the new get and set methods
Note that the variables 'Type' and 'var' are available as standard variables in the closure allowing for programmatic manipulation of the values

```
    java.template("private \${Type} \${var};", {
        java.addMethod("public void set" + var.substring(0,1).toUpperCase() + var.substring(1) + "(" + Type + " " + var +") { this." + var + "=" + var + "; }")
        java.addMethod("public " + Type + " get" + var.substring(0,1).toUpperCase() + var.substring(1) + "() { return this." + var + ";}")
    });
```

The following example shows how to automatically add the private logger field (if it doesn't exist). And then invoke the logger method. In standard eclipse you would 
need to template the field and method call seperately. If the field already exists it won't be re-added

```
    java.addImport("org.slf4j.Logger");
    java.addImport("org.slf4j.LoggerFactory");
    java.addFieldFirst("private static final Logger LOGGER = LoggerFactory.getLogger(" + java.className +");");
    java.template("LOGGER.info(\"\${msg}\");\${cursor}");
```

##API

The 'java' binding is provided for easier java manipulation. If you need something not supported feel free to add or request it. You can also access the current eclipse editor
directly via the 'editor' binding to do whatever you want (which is what JavaManipulation uses anyway)


```
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
     * at the top of the class
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
  }
```
