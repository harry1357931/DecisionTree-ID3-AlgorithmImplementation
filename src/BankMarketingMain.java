
/* Class BankMarketingMain
 * Final Project
 * Machine Learning - CS 3813
 * Description: 
 *    1) Builds Decision Tree using Training Data 
 *    2) Tests Data and Output Final Reports related to accurate predictions
 * @param data  a Data Class Object that manages all queries relating 
 * to Reading and Storing of Data points from  text files
 * @param DecisionTree  a Tree Class Object that builds Decision Tree using Training Data, 
 * Tests Data and Output Final Reports related to accurate predictions 
 * 
 * @author Gurpreet Singh 
 */
public class BankMarketingMain {
    
	public static Data[] data = new Data[4];
	public static Tree[] DecisionTree = new Tree[4];
    
	public static void main(String[] args) {
	   
	  for(int i=0; i < 4; i++ ){	
		 data[i] = new Data(i, 80-i*20);                 
		 int BestFeatIdAtRoot =  data[i].BestInfoGainFromRemainingFeatures(data[i].TrainingSet); 
	     DecisionTree[i] = new Tree(BestFeatIdAtRoot);           // Creates Root of Tree
		 DecisionTree[i].BuildTreeAfterRoot(data[i].TrainingSet, DecisionTree[i].root);      
		 DecisionTree[i].CountAndCheckAllTestingDataPoints(DecisionTree[i].root, data[i].TrainingSet, data[i].TestingSet);
	     data[i].OutputFinalReport();
	     ResettingValues();
	     
	   } 
				
	}// Main Function
	
	public static void ResettingValues(){
		for(int i=0; i<16; i++){
		    Tree.FeatNotUsed[i] = i;
	    }
		Data.AccurateTrainingCount = 0;
		Data.AccurateTestingCount = 0;
		Data.BREAKPOINT =0;
	}

}// Class BankMarketingMain
