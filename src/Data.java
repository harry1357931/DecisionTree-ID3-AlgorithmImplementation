/* Class Data
 * Final Project
 * Machine Learning - CS 3813
 * **********
 * Parameters
 * **********
 * @param Features  an array storing name of attributes
 * @param FeatureType an array storing Type of attributes: Categorical or Numerical  
 * @param FeatureValues  a Multidimensional array to Store Feature Values of Attributes  
 * @param FeatMain  reads the whole file Containing info. about features
 * @param MainDataSet Stores the main Data Set
 * @param Classes  an array that Stores the Values to be predicted ("yes" or "no") 
 * @param AccurateTrainingCount  Count the accurate Training Predictions
 * @param AccurateTestingCount   Count the accurate Testing Predictions
 * @param BREAKPOINT  Stores the recently calculated value of Break Point 
 * ************
 * Author Name:
 * ************
 * @author Gurpreet Singh 
 */
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Data {
	
	public StringTokenizer myTokens;
	public static String fileName =  "bankFullComma.txt";
	public double TrainingPercent;
	public static int TotalDataPoints = CountTotalDataPointsInFile(fileName);
	public int NumOfTrainingDataPoints;
	public int[] TrainingIndexes;
	
	// BankMarketing Data Set
	public static String[][] MainDataSet = new String[TotalDataPoints][17];                   // sub array length 17
    public String[][] TrainingSet;                                    
    public String[][] TestingSet;   
    	
	// Feature related Data
	public static String[] Features = new String[16];      
    public static String[] FeatureType = new String[16];   
    public static String[][] FeatureValues = new String[16][];       // max sub array length 12
    public static String[][] FeatMain = new String[16][];              // max sub array length 14
    
    public static String[] Classes = {"yes", "no"};
    
    // Accurate Prediction related Variables
    public static int AccurateTrainingCount=0;
    public static int AccurateTestingCount=0;
    public static double BREAKPOINT=0;

    public Data(int id, double TrainingPerc){                                    // Data Constructor
       if(id == 0){	
    	 FillingFeatureValues();
    	 MainDataSet = readFile(fileName, TotalDataPoints, 17);
       }
        
       TrainingPercent = TrainingPerc;
       NumOfTrainingDataPoints = (int)((float)(TrainingPercent*TotalDataPoints*0.01));
       TrainingIndexes = new int[NumOfTrainingDataPoints];
       
       TrainingSet = new String[NumOfTrainingDataPoints][17];
       TestingSet = new String[TotalDataPoints-NumOfTrainingDataPoints][17]; 
       FillingRandomlyTrainingAndTestingSets(TrainingPerc);
    	
    }// Constructor
    
    public void FillingRandomlyTrainingAndTestingSets(double TrainingPercent){
          
    	TrainingSet = readFile("Train"+(int)(TrainingPercent)+".txt", TrainingSet.length, 17);
    	TestingSet = readFile("Test"+(int)(100-TrainingPercent)+".txt", TestingSet.length, 17);         
         
    }
    
    public boolean NumExistsInArray(int NumToBeChecked, int UpToIndex){
    	if(UpToIndex == 0)
    		return false;
    	
    	for(int i=0; i< UpToIndex; i++){
    		if(TrainingIndexes[i]== NumToBeChecked){
    			return true;
    		}
    	}
    	
    	return false;
     }

    public double getPercentageAccuracy(String SetType){
    	double percentage=0;
    	if(SetType.equals("Training") == true){
    		percentage = AccurateTrainingCount*1.0/NumOfTrainingDataPoints;
    	}else if(SetType.equals("Testing") == true){
    		percentage = AccurateTestingCount*1.0/(TotalDataPoints-NumOfTrainingDataPoints);
        }else if(SetType.equals("Combined") == true){
    		percentage = (AccurateTrainingCount+AccurateTestingCount)*1.0/TotalDataPoints;
    	}
    	return percentage*100;
    }
    
    public int BestInfoGainFromRemainingFeatures(String[][] DataSubset){             // return the best InfoGain Feature_Id at some node...
		double highIG=-1;
		int highestIgFeatId=-1;
		double[] IG = new double[16];
	     
		for(int i=0; i<IG.length; i++){	                         // Getting Info Gain for all Features
		   IG[i] = InfoGain(i, DataSubset);                      // i: feature_id
		}
		
		for(int i=0; i<IG.length; i++){	                         // Getting Info Gain for all Features
			   System.out.println(FeatureType[i]+"  "+i+" : "+IG[i]);                      // i: feature_id
		}
		
		for(int i=0; i<IG.length; i++){	              
		   if(Tree.FeatNotUsed[i] == -1)        
			   continue;
		   
		   if(highIG < IG[i]){
			   highIG = IG[i];
			   highestIgFeatId = i;
		   }
		}
		
		return highestIgFeatId;        
	}
	
  public double InfoGain(int feature_id, String[][] rootArray){           // For Testing Purpose: let feature_id = 1
	    
		double infogain=0, EntropyBeforeFeature=0, EntropyAfterFeature=0, Prob=0;
		int NumElementsInRootArray = CountNumElements(rootArray);           
		EntropyBeforeFeature = CalculateEntropy(rootArray);                // EntropyBeforeFeature 
		
		if(FeatureType[feature_id].equalsIgnoreCase("numeric")){
			 EntropyAfterFeature = CalcEntropyAfterContinuousFeature(rootArray, feature_id);
			
		}else if(FeatureType[feature_id].equalsIgnoreCase("categorical"))
		{
		   
	      for(int i=0; i < FeatureValues[feature_id].length; i++){         
		    
			if(FeatureValues[feature_id][i] == null){        
				break;                                       
			}
				
		    String[][] UpdatedArray = updatedArray(rootArray, feature_id, FeatureValues[feature_id][i]);
			if(UpdatedArray==null)
			   Prob=0;                                                   //Prob:  GainInfo = H(S-root) - Summation(Prob*H(S))...
			else
			   Prob = (UpdatedArray.length*1.0)/NumElementsInRootArray;
			
			EntropyAfterFeature= EntropyAfterFeature + Prob*CalculateEntropy(UpdatedArray);  // Entropy After feature
		
		  }// for loop 		
		
		}// else 
		
		infogain = EntropyBeforeFeature - EntropyAfterFeature;         
	    return infogain;                                        
   	
   }   // Info gain function
  
    public double CalcEntropyAfterContinuousFeature(String[][] rootArray, int feature_id){
    	double EntropyAfterFeature = 0, BreakPoint =0, Prob=0; 
    	
    	for(int i=0; i< rootArray.length; i++){
    	   BreakPoint = BreakPoint + Integer.parseInt(rootArray[i][feature_id]);	
    	}
    	
    	BreakPoint = BreakPoint/rootArray.length;
    	
    	System.out.println("FeatureId: "+feature_id+ "   BreakPoint: "+BreakPoint);
    	
    	int[] LessThanBreakPoint = {0,0};              // 0: yes, 1: no 
    	int[] GreaterThanEqualToBreakPoint = {0,0};    // 0: yes, 1: no
    	
    	for(int i=0; i< rootArray.length; i++){
    	   if(Integer.parseInt(rootArray[i][feature_id]) < BreakPoint){
    		   if(rootArray[i][16].equalsIgnoreCase("yes")){
    			   LessThanBreakPoint[0]++;
    		   }
    		   else                        // if "no"
    		   {   LessThanBreakPoint[1]++; 
    	       }
    	   }
    	   else if(Integer.parseInt(rootArray[i][feature_id]) >= BreakPoint){
    		   if(rootArray[i][16].equalsIgnoreCase("yes")){
    			   GreaterThanEqualToBreakPoint[0]++;
    		   }
    		   else                      // if "no"
    		   {   GreaterThanEqualToBreakPoint[1]++; 
    	       }
    	   }
    	} // for 
        
        Prob = (sumArray(LessThanBreakPoint)*1.0)/(rootArray.length*1.0); 
        EntropyAfterFeature = Prob*EntropySub(LessThanBreakPoint);
    	Prob = (sumArray(GreaterThanEqualToBreakPoint)*1.0)/(rootArray.length*1.0);
    	EntropyAfterFeature = EntropyAfterFeature + Prob*EntropySub(GreaterThanEqualToBreakPoint);
    	System.out.println("Entropy after Greater: " + EntropyAfterFeature);
    	return EntropyAfterFeature;
    }
  
   public double EntropySub(int[] x){             //x: Class results array...[1, 2, 0, 1] = [unacc, acc, good, vgood]
      double entropy=0, Prob, ArraySum;
      ArraySum = sumArray(x);
      if(ArraySum==0)       
         return 0;
		for(int i=0; i<x.length; i++){
			Prob = x[i]/ArraySum;             
			entropy = entropy + (Prob)*log2(Prob);
	    }
		return -entropy;
	} 
	     
	public double CalculateEntropy(String[][] dataSubset){              // Calculate Entropy from main String array
		return EntropySub(ClassTargetValueCount(dataSubset));
	}   
		
	public static int[] ClassTargetValueCount(String[][] upd){
		// 0:yes, 1:no
		int[] AllClassesCount = {0,0};
		if(upd==null){
			// if array returned by updated method is NULL...or if SubSet is NULL...
 	    }else{
		  for(int i=0; i<upd.length; i++ ){
			if(upd[i][1]==null){
			   break;	
		    }
			else if(upd[i][16].equals("yes")){
				AllClassesCount[0]++;              //yes++
			}
			else if(upd[i][16].equals("no")){
				AllClassesCount[1]++;              //no++
			}
			
		 } // for loop ... 
	    } // else ... 	
		return AllClassesCount;
	}
	
	public String[][] updatedArray(String[][] extractFrom, int feat_id, String feat_val){
		int count=0;
		
		if(extractFrom == null)
			return null;

		for(int i=0; i < extractFrom.length; i++){            // For loop to count the datapoints in new sub set
			if(extractFrom[i][1] == null)
				break;
			if(extractFrom[i][feat_id].equals(feat_val))      // use here "feature_id" and "value"
				count++;
	    }
		if(count==0)     
		   return null;
		
		String[][] DataSubset = new String[count][17];         // "count" will give us the precise array
		count=0;
		for(int i=0; i < extractFrom.length; i++){
			if(extractFrom[i][1] == null)
				break;
			if(extractFrom[i][feat_id].equals(feat_val)){     // use here "feature_id" and "value"
				DataSubset[count] = extractFrom[i];
				count++;
			}
		}
		return DataSubset;
	}
	
	public int CountNumElements(String[][] ToCountIn){
		if(ToCountIn == null){
			return 0;
		}
        
		int count=0;
	    for(int i=0; i<ToCountIn.length;i++){             
	    	if(ToCountIn[i][1]==null){
	    		break;
	    	}
	    	count++;
	    }	
		return count;
	}
	
	public static int CountTotalDataPointsInFile(String fileName)        // length of array to be filled  
	{   
	    int CountLine=0;
	    TextFileInput tfi = new TextFileInput(fileName);
		String line = tfi.readLine(); 
	    while(line!=null)
		{	line=tfi.readLine();
			CountLine++;
		}
		return CountLine;                                         
    }// Read File method 
	
	public int sumArray(int[] x){
	   	int sum=0;
		for(int i=0; i< x.length; i++){
	       sum= sum+x[i];  
	    }
		return sum;
	}
	
	public static double log2(double num)
	{ if(num==0)
		return 0;
	  else 
	    return (Math.log(num)/Math.log(2));
	}
	
    public void FillingFeatureValues(){
    	FeatMain = readFile("Feature_Values.txt", FeatMain.length, -1);
    	
    	for(int i=0; i<FeatMain.length; i++){
    	    Features[i] = FeatMain[i][0];
    	    FeatureType[i] = FeatMain[i][1];
    	    FeatureValues[i] = new String[FeatMain[i].length-2];
    	    System.out.print("\n"+Features[i]+","+FeatureType[i]+",");
    	    
    	    for(int j=2; j<FeatMain[i].length; j++){
    	    	FeatureValues[i][j-2] = FeatMain[i][j];
    	       System.out.print(FeatureValues[i][j-2]+",");    	
    	    } 
    	 
    	}  // for i
    	 
    }  // FillingFeatureValues 
    
    public String[][] readFile(String fileName, int arraylength, int SubArrayLength)        // length of array to be filled  
	{   
	    String[][] loaded = new String[arraylength][];         //SubArrayLength
	    
	    TextFileInput tfi = new TextFileInput(fileName);    
		String line = tfi.readLine(); 
	    int count=0;
		while(line!=null)
		{	myTokens = new StringTokenizer(line,",");
		    if(SubArrayLength == -1) 
		       loaded[count] = new String[myTokens.countTokens()];
		    else
		       loaded[count] = new String[SubArrayLength];	
		    int j=0;
			while(myTokens.hasMoreTokens()){
			   loaded[count][j] = 	myTokens.nextToken();
			   j++;
			}
			
			count++;      
			line=tfi.readLine();
		}// while loop 
		
		return loaded;           // "loaded" array is an array formed by reading from file...
  	}// Read File method 
    
    
    public void OutputFinalReport(){
    	System.out.println("Total DataPoints: "+ TotalDataPoints+"\n");
    	System.out.println("Training Set: "+TrainingPercent+"%");
    	System.out.println("Data Points in Training Set: "+NumOfTrainingDataPoints+"\n");
    	System.out.println("Testing Set: "+(100-TrainingPercent)+"%");
    	System.out.println("Data Points in Testing Set: "+(TotalDataPoints-NumOfTrainingDataPoints+"\n"));
    	System.out.println("The Decision Tree is trained with "+TrainingPercent+"% Training Set"+"\n");
    	System.out.println("Training Set Accuracy Results = "+getPercentageAccuracy("Training")+"%");
    	System.out.println("Total Training Datapoints Tested: "+NumOfTrainingDataPoints);
    	System.out.println("No. of Training Datapoint Accurate Predictions: "+AccurateTrainingCount+"\n");
    	System.out.println("Testing Set Accuracy Results = "+getPercentageAccuracy("Testing")+"%");
    	System.out.println("Total Testing Datapoints Tested: "+(TotalDataPoints-NumOfTrainingDataPoints));
    	System.out.println("No. of Testing Datapoints Accurate Predictions: "+AccurateTestingCount+"\n");
        System.out.println("Combined Accuracy: "+getPercentageAccuracy("Combined")+"\n");
    	System.out.println("To Run the Program with different Training Percentage:-");
    	System.out.println("Change the Value of Static Variable 'TrainingPercent' to one of 20,40,60,80 in Class 'Data' and Run the Program Again...");
    	
    	StringBuilder builder= new StringBuilder();
	    
    	builder.append("Bank Marketing Project by: Gurpreet Singh\n\n");
    	builder.append("First Classification Approach: Decision Tree Implementation \n\n");
    	builder.append("Classification Goal: To Predict whether the client will subscribe a Term Deposit or Not \n\n");
    	
    	builder.append("Total DataPoints: "+ TotalDataPoints+"\n");
    	builder.append("Number of Attributes: 16 + Output Attribute\n");
    	builder.append("Types of Attributes: 10 Categorical and 7 Numeric Attributes \n\n");
    	
		// X
		
		builder.append("Training Set: "+(100-TrainingPercent)+"%\n");
		builder.append("Data Points in Training Set: "+(TotalDataPoints-NumOfTrainingDataPoints)+"\n\n");
		builder.append("Testing Set: "+TrainingPercent+"%\n");
		builder.append("Data Points in Testing Set: "+NumOfTrainingDataPoints+"\n\n");
		builder.append("The Decision Tree is trained with "+(100-TrainingPercent)+"% Training Set"+"\n");
		builder.append("Training Set Accuracy Rate = "+getPercentageAccuracy("Training")+"%\n");
		builder.append("Total Training Datapoints Tested: "+(TotalDataPoints-NumOfTrainingDataPoints)+"\n");
		builder.append("No. of Training Datapoint Accurate Predictions: "+(int)((TotalDataPoints-NumOfTrainingDataPoints)*getPercentageAccuracy("Training")*0.01)+"\n\n");
		builder.append("Testing Set Accuracy Rate = "+getPercentageAccuracy("Testing")+"%"+"\n");
		builder.append("Total Testing Datapoints Tested: "+NumOfTrainingDataPoints+"\n");
		builder.append("No. of Testing Datapoints Accurate Predictions: "+(int)(NumOfTrainingDataPoints*getPercentageAccuracy("Testing")*0.01)+"\n\n");
		builder.append("Combined (Training + Testing)Set Accuracy Rate: "+(float)(((int)(NumOfTrainingDataPoints*getPercentageAccuracy("Testing")*0.01)
				        +(int)((TotalDataPoints-NumOfTrainingDataPoints)*getPercentageAccuracy("Training")*0.01))*100*0.1/(TotalDataPoints*0.1)) +"%\n");
		builder.append("To Run the Program with different Training Percentage:-\n");
		builder.append("Change the Value of Static Variable 'TrainingPercent' in Class 'Data' to one of 20, 40, 60, 80 percent  and then Run the Program Again...");
		 
		JOptionPane.showMessageDialog(null, builder.toString());
    }

}// Class Data
