/* Class TreeNode
 * Machine Learning - CS 3813
 * To Dr.Changhe  Yuan
 * Builds a TreeNode
 * 
 * @param parent reference to parent node
 * @param children  TreeNode Array..containing reference to children Nodes
 * @param Edge  Reference to Edge Names     
 * @param ChildCount Counts the Child
 * @param TestingDataSubset StringArray to Store the Testing Data Subset
 * @param feature_id  Stores the Atrribute Id or Feature id 
 * @param isLeaf Tells whether that Node is Leaf Node or Not
 * @param ClassId ClassId is used if it is a LeafNode
 * @param name String: To Store the name of Node 
 * @param BreakPoint  break point for continuous features
 * 
 * @author Gurpreet Singh 
 */


public class TreeNode {
	
	public TreeNode parent;
	public TreeNode[] children = new TreeNode[13];           // "children" : one dimensional array of children
	public String[] Edge = new String[13];                   // Contains the Edges Connecting Parent and Child...
	public int ChildCount=0;
	
	public int feature_id;                                   // "feature_id" of Feature of this node...
	public int ClassId;                                      // For Leaf node
    public boolean isLeaf;                                   // To check...whether node is leaf node or not
    public String name;
    public double BreakPoint;                                // for Numeric feature...
    
    public TreeNode(int feat_id, TreeNode Parent) {           // Constructor for Internal Nodes
	   
	    feature_id = feat_id;
	    parent = Parent;
	    Tree.FeatNotUsed[feat_id]= -1;      
	    isLeaf =false;
	    name = Data.Features[feat_id];              // need Change here...a little...static problem
	}
    
    public TreeNode(int Class_Id){                              // Constructor for Leaf Node
    	isLeaf = true;
    	ClassId = Class_Id;
    	name = Data.Classes[Class_Id];             //one of "yes" or "no"
    } 
       
}    // Class Tree Node ends here...
