
/* Class Tree
 * Machine Learning - CS 3813
 * To Dr.Changhe  Yuan
 * Builds a TreeNode
 * @param root Stores the reference to root Node
 * @param FeatNotUsed Update on the Status of which Feature is used and Which is not...
 * 
 * @author Gurpreet Singh 
 */

public class Tree {
	   
	   public TreeNode root;
	   public static int[] FeatNotUsed = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};       // If feature used: set FeatNotUsed[feature_id] = -1
	
	   public Tree(int feat_id){          // Constructor....Creates Root Node...
		  
		   root = new TreeNode(feat_id, null);             // root node
	       
	   }
	   
	   public String[][] UpdatedArrayForNumeric(String[][] extractFrom, int feature_id, int ZeroOrOne){
		   
		   if(extractFrom == null)
				return null;
		   
		   int count=0;
		   double BreakPoint =0; 
	       for(int i=0; i< extractFrom.length; i++){
	    	   BreakPoint = BreakPoint + Integer.parseInt(extractFrom[i][feature_id]);	
	       }
	       BreakPoint = BreakPoint/extractFrom.length;
	       Data.BREAKPOINT = BreakPoint;
	        
	       if(ZeroOrOne == 0){            //Less than BreakPoint
	    	    int CountLessThanBreakPoint=0;
	    	    for(int i=0; i< extractFrom.length; i++){
	    	      if(Integer.parseInt(extractFrom[i][feature_id]) < BreakPoint){
	    		         CountLessThanBreakPoint++;
	    	       }
	    	    }  // for loop ends here...
	    	    
	    	    count = CountLessThanBreakPoint;
	    	    
	        }
	    	else          // if ZeroOrOne ==1        //Greater than BreakPoint
	    	{	int CountGreaterThanEqualToBreakPoint=0;
	    		for(int i=0; i< extractFrom.length; i++){
	 	    	   if(Integer.parseInt(extractFrom[i][feature_id]) >= BreakPoint){
	 	    		   CountGreaterThanEqualToBreakPoint++;
	 	    	   }
	 	    	} // for loop ends here
	    	    count = CountGreaterThanEqualToBreakPoint; 	
	        } // else ends here...
	       
	       if(count == 0)       
 			   return null;
 			
 			String[][] DataSubset = new String[count][17];
	       
	        count=0;
	        if(ZeroOrOne == 0){
			   for(int i=0; i < extractFrom.length; i++){  
		          if(Integer.parseInt(extractFrom[i][feature_id]) < BreakPoint){             
				   	 DataSubset[count] = extractFrom[i];
					 count++;
				  }
			    }  // for loop ends here...
			}
	        else
	        {
	        	for(int i=0; i < extractFrom.length; i++){  
			       if(Integer.parseInt(extractFrom[i][feature_id]) >= BreakPoint){             
					  DataSubset[count] = extractFrom[i];
				      count++;
				   }
				}
	        } // else ends here...
			
			return DataSubset;
		   
	   } // function ends here....
       
	   public void BuildTreeAfterRoot(String[][] DataSubset, TreeNode ParentRoot){
		   
		   String[][] NextDataSubset;
		   int Feat_id = -1;
		   
		   if(Data.FeatureType[ParentRoot.feature_id].equalsIgnoreCase("numeric")){
		      
			  for(int i=0; i<2; i++){
			    NextDataSubset = UpdatedArrayForNumeric(DataSubset, ParentRoot.feature_id, i );        // i=0 for less than, i= 1 for greater than
			    if(NextDataSubset == null){
		             continue;                 // i.e. we neither want child neither leaf...that branch is cut...
		        }
			    Feat_id = BestInfoGainFromRemainingFeatures(NextDataSubset);    // Feat_id for node...about to be Constructed
		        if(Feat_id == -1){                                       // i.e. Info gain of ALL features = 0, so we don't want node...
			    	   int classId = getClass_Id(NextDataSubset);     // may need check here...
			    	   if(classId == -1){
			    		   continue;
			    	   }
			    	   
			    	   TreeNode child = new TreeNode(classId);             // Leaf Node
			    	   ParentRoot.BreakPoint = Data.BREAKPOINT;
			    	   ParentRoot.children[ParentRoot.ChildCount] = child;
					   ParentRoot.ChildCount++;                            // Increasing Child of Parent...
	                   continue;
			        }
			        
		          ParentRoot.BreakPoint = Data.BREAKPOINT;
				  TreeNode child = new TreeNode(Feat_id, ParentRoot);   // Need change here...assign breakpoint in parent node    //Data.BREAKPOINT is updated in UpdatedArrayForNumeric function
				  ParentRoot.children[ParentRoot.ChildCount] = child;
				  ParentRoot.ChildCount++;                                // Increasing Child of Parent...
				   
				  BuildTreeAfterRoot(NextDataSubset, child);              // Recursive Call...
				  FeatNotUsed[Feat_id]= Feat_id;                          // Feature_Id ready to use again...

  			  } // for loop ends here...
		   }
		   else
		   { for(int i=0; i < Data.FeatureValues[ParentRoot.feature_id].length; i++){    // Need Change Here...
		        if(Data.FeatureValues[ParentRoot.feature_id][i]== null){     // If feature Value is Null in Feature_values.txt
				    continue;
			    }
		        
		        NextDataSubset =  updatedArray(DataSubset, ParentRoot.feature_id, Data.FeatureValues[ParentRoot.feature_id][i] );
		        
		        if(NextDataSubset == null){
		             continue;                 // i.e. we neither want child neither leaf...that branch is cut...
		        }
		        Feat_id = BestInfoGainFromRemainingFeatures(NextDataSubset);    // Feat_id for node...about to be Constructed
		       
		        if(Feat_id == -1){                                       // i.e. Info gain of ALL features = 0, so we don't want node...
		    	   int classId = getClass_Id(NextDataSubset);
		    	   if(classId == -1){
		    		   continue;
		    	   }	   
		    	   TreeNode child = new TreeNode(classId);             // Leaf Node
		           ParentRoot.Edge[ParentRoot.ChildCount] =  Data.FeatureValues[ParentRoot.feature_id][i];
		    	   ParentRoot.children[ParentRoot.ChildCount] = child;
				   ParentRoot.ChildCount++;                            // Increasing Child of Parent...
                   continue;
		        }
		       
			    TreeNode child = new TreeNode(Feat_id, ParentRoot);
			    ParentRoot.children[ParentRoot.ChildCount] = child;
			    ParentRoot.Edge[ParentRoot.ChildCount] =  Data.FeatureValues[ParentRoot.feature_id][i];
			    ParentRoot.ChildCount++;                                // Increasing Child of Parent...
			   
			    BuildTreeAfterRoot( NextDataSubset, child );            // Recursive Call...
			    FeatNotUsed[Feat_id]= Feat_id;                          // Feature_Id ready to use again...
		    }
		 }// else ends here...  
		   
		   System.out.println("Calculating...Wait...");   
	   }
	   	   
	   // Recursive Function ....Unchecked...Check it now..
	   public boolean TestingDataPoint(TreeNode Node, String[] Testing1){  //Receives only the Datapoint from whole Data set
		                                                             //  [low,high,4,more,big,high,vgood]  --- 1
		   // add the numeric point of view...and modify indexes...
		   int index = -1;
		   boolean ToReturn = false;
		   
		   if(Data.FeatureType[Node.feature_id].equalsIgnoreCase("numeric"))
		   {  
			   //System.out.println("Value: "+Integer.parseInt(Testing1[Node.feature_id]));
			   if(Integer.parseInt(Testing1[Node.feature_id]) < Node.BreakPoint ){
				   index = 0;
			   }
			   else{
				   index = 1;   
			   }
			   
			   if(Node.ChildCount == 1){           // if child count == 1, then that child can be "greater than Break Point too...think...
				                           //if there is no datapoint  in "less than break point"...then 1st child will be "greater than break point" with index = 0 
				   index=0;
			   }
			   
		   }else
		   { for(int i=0; i < 13; i++){   
			   
			   if(Node.Edge[i] == null)                             // if that Edge don't exists in Tree due to Short Training Set....needed for Testing set
				   return false;
			   if( Node.Edge[i].equals(Testing1[Node.feature_id])== true ){
				   index = i;
				   break;
			   }
		     }
		   
		   }   // else ends here...
		     
		   if(Node.children[index].isLeaf == true){
			   
			 
			   if(Node.children[index].name.equals(Testing1[16]) == true) 
				   return true;
			   else
				   return false; 
		   }
		   else{
			   ToReturn = TestingDataPoint(Node.children[index], Testing1);  
		   }
		   
		   return ToReturn;
	   }
	   
	    
	   
	   public void CountAndCheckAllTestingDataPoints(TreeNode root ,String[][] Training, String[][] Testing){
		   
		   for(int i=0; i< Training.length; i++){
			    //passed only the DataPoint from whole Data set
		       if(TestingDataPoint(root, Training[i])== true){
		    	     Data.AccurateTrainingCount++;
		       }
		   }
		   System.out.println("Calculating...Wait...");
		   
		   //int NumOfTesting = CountNumElements(Testing);
		   
		   for(int i=0; i< Testing.length; i++){
		       if(TestingDataPoint(root, Testing[i])== true){
		    	   Data.AccurateTestingCount++; 
		       }
		   }
	   }
	   
	   public String[][] updatedArray(String[][] extractFrom, int feat_id, String feat_val){
			int count=0;
			// Check whether extractFrom array is null or not...
			if(extractFrom == null)
				return null;
			
			for(int i=0; i < extractFrom.length; i++){                    // For loop to count the datapoints in new sub set
				if(extractFrom[i][1] == null)
					break;
				if(extractFrom[i][feat_id].equals(feat_val))              // use here "feature_id" and "value"
					count++;
		    }
			if(count==0)       
			   return null;
			
			String[][] DataSubset = new String[count][17];                 // "count" will give us the precise array
			count=0;
			for(int i=0; i < extractFrom.length; i++){  
				if(extractFrom[i][1] == null)
					break;
				if(extractFrom[i][feat_id].equals(feat_val)){             // use here "feature_id" and "value"
					DataSubset[count] = extractFrom[i];
					count++;
				}
			}
			return DataSubset;
		}
	   
	   
	    public int getClass_Id(String[][] OneClassArray){    
	    	int[] ClassCount = ClassTargetValueCount(OneClassArray);
	    	
	    	for(int i=0; i<ClassCount.length;i++){     //need change here...may be...
	           if(ClassCount[i] != 0)
	        	   return i;
	    	}
	      return -1; 	
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
				
			 } // for loop ends here... 
		    } // else ends here... 	
			return AllClassesCount;
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
		
		public double InfoGain(int feature_id, String[][] rootArray){           // For Testing Purpose: let feature_id = 1
		    
			if(rootArray == null){
			   return 0;
			}   
			double infogain=0, EntropyBeforeFeature=0, EntropyAfterFeature=0, Prob=0;
			int NumElementsInRootArray = CountNumElements(rootArray);
			EntropyBeforeFeature = CalculateEntropy(rootArray);                // EntropyBeforeFeature 
			
			
			if(Data.FeatureType[feature_id].equalsIgnoreCase("numeric")){
				 EntropyAfterFeature = CalcEntropyAfterContinuousFeature(rootArray, feature_id);
				
			}else if(Data.FeatureType[feature_id].equalsIgnoreCase("categorical"))
			{
			
			  for(int i=0; i< Data.FeatureValues[feature_id].length; i++){           // need Change here....sub array length...
			    if(Data.FeatureValues[feature_id][i] == null){    
					break;
				}
				
				String[][] UpdatedArray = updatedArray(rootArray, feature_id, Data.FeatureValues[feature_id][i]);
				if(UpdatedArray==null)
				   Prob=0;                                                   //Prob:  GainInfo = H(S-root) - Summation(Prob*H(S))...
				else
				   Prob = (UpdatedArray.length*1.0)/NumElementsInRootArray;
				
				EntropyAfterFeature= EntropyAfterFeature + Prob*CalculateEntropy(UpdatedArray);  // Entropy After feature
			
			 }// for loop ends here		
			
			}// else ends here
			
			infogain = EntropyBeforeFeature - EntropyAfterFeature;
		    return infogain;
	   	}
		
		public double CalcEntropyAfterContinuousFeature(String[][] rootArray, int feature_id){
	    	
			if(rootArray == null){
			     return 0;
			}	
			double EntropyAfterFeature = 0, BreakPoint =0, Prob=0; 
	    	
	    	for(int i=0; i< rootArray.length; i++){
	    	   BreakPoint = BreakPoint + Integer.parseInt(rootArray[i][feature_id]);	
	    	}
	    	
	    	BreakPoint = BreakPoint/rootArray.length;
	    	
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
	    	} // for loop ends here
	        
	    	Prob = (sumArray(LessThanBreakPoint)*1.0)/(rootArray.length*1.0);
	    	EntropyAfterFeature = Prob*EntropySub(LessThanBreakPoint);
	        Prob = (sumArray(GreaterThanEqualToBreakPoint)*1.0)/(rootArray.length*1.0);
	    	EntropyAfterFeature = EntropyAfterFeature + Prob*EntropySub(GreaterThanEqualToBreakPoint);
	        return EntropyAfterFeature;
	    }

		

		
		public int CountNumElements(String[][] ToCountIn){
			if(ToCountIn == null){
				return 0;
			}
	        int count=0,i=0;
		    while(i < ToCountIn.length){	
			    if(ToCountIn[i][1]==null){
		    		break;
		    	}
		    	count++;
		    	i++;
		    }	
		    return count;
		}
		
		public int BestInfoGainFromRemainingFeatures(String[][] DataSubset){             // return the best InfoGain Feature_Id at some node...
			    			
			double highIG=-1, SumIG=0;
			int highestIgFeatId=-1;
			double[] IG = new double[16];
		    
			for(int i=0; i<IG.length; i++){	                                    // Getting Info Gain for all Features
			   IG[i] = InfoGain(i, DataSubset);                         // i: feature_id
			   SumIG = SumIG + IG[i];
			}
			
		    if(SumIG!=0){
			  for(int i=0; i<IG.length; i++){	
			    if(FeatNotUsed[i] == -1)                     
				   continue;
			   
			    if(highIG < IG[i]){
				   highIG = IG[i];
				   highestIgFeatId = i;
			     }
			  }
			}// if ends here 
		    
		return highestIgFeatId;        
		
	 }// Best Info Gain ends here...	
	
}// Tree Class Ends here...
