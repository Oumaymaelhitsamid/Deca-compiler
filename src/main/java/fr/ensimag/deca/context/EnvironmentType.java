package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.util.Map;
import java.util.HashMap;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl44
 * @date 01/01/2022
 */
public class EnvironmentType extends EnvironmentExp {

    private Map<Symbol, Definition> map = new HashMap<Symbol, Definition>();
    
    public EnvironmentType(DecacCompiler compiler) throws ContextualError {
        super(null);
        SymbolTable table = compiler.getSymbolTable();

        try {

            this.declare(table.create("void"), new TypeDefinition(new VoidType(table.create("void")), Location.BUILTIN));
            this.declare(table.create("int"), new TypeDefinition(new IntType(table.create("int")), Location.BUILTIN));
            this.declare(table.create("boolean"), new TypeDefinition(new BooleanType(table.create("boolean")), Location.BUILTIN));
            this.declare(table.create("float"), new TypeDefinition(new FloatType(table.create("float")), Location.BUILTIN));

            this.declare(table.create("string"), new TypeDefinition(new StringType(table.create("string")), Location.BUILTIN));

            Symbol object = table.create("Object");
            ClassDefinition objectDef = new ClassDefinition(new ClassType(object, Location.BUILTIN, null), Location.BUILTIN, null);
            objectDef.setLabel(new Label(objectDef.getType().getName().toString() + "." + compiler.getUniqueID()));
            Signature equalsSignature = new Signature();
            equalsSignature.add(objectDef.getType());
            MethodDefinition objectEquals = new MethodDefinition(this.get(table.create("boolean")).getType(), Location.BUILTIN, equalsSignature, objectDef.incNumberOfMethods());
            objectEquals.setLabel(new Label(objectDef.getLabel().getName() + ".equals." + compiler.getUniqueID()));
            objectDef.getEnvironment().declare(table.create("equals"), objectEquals);

            this.declare(object, objectDef);

        } catch (DoubleDefException e) {
            // Unreachable error in principle
            throw new ContextualError("[Unreachable Error] Couldn't define built-in types", new Location(-1, -1, "<no source file>")); 
        }  
        
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public Definition get(Symbol key) {
        return this.map.get(key);
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     * 
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary 
     * - or, hides the previous declaration otherwise.
     * 
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, Definition def) throws DoubleDefException {
        if (this.map.containsKey(name)) {
            throw new DoubleDefException();
        }
        this.map.put(name, def);
    }

}
