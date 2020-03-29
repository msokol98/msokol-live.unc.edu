package SKPLIST_A4;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SkipList implements SkipList_Interface {
  private SkipList_Node root;
  private final Random rand;
  private double probability;
  private final int MAXHEIGHT = 30; // the most links that a data cell may contain
  
  private final int maxHeight;
  private int size;
  private int level;

  public SkipList(int maxHeight) {
	  
	size = 0;
	level = 0;
	
	if(maxHeight > MAXHEIGHT) {maxHeight = MAXHEIGHT;}
	this.maxHeight = maxHeight;
	
    root = new SkipList_Node(Double.NaN, maxHeight);
    rand = new Random();
    probability = 0.5;
  }

  @Override
  public void setSeed(long seed) { rand.setSeed(seed); }
  
  @Override
  public void setProbability(double probability) { 
     this.probability = probability; 
  }
  
  private boolean flip() {
    // use this where you "roll the dice"
    // call it repeatedly until you determine the level
    // for a new node
    return rand.nextDouble() < probability;
  }
  
  private int randomLevel() {
	  int newLevel = 0;
	  
	  while(flip()) {
		  newLevel++;
	  }
	  
	  level = Math.max(level, newLevel);
	  
	  return newLevel;
  }
  
  @Override
  public SkipList_Node getRoot() { return root; }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    int levels;
    for(levels = 0; levels < root.getNext().length && root.getNext(levels) != null; levels ++);
    
    StringBuilder[] sbs = new StringBuilder[levels];
    
    for(int i = 0; i < sbs.length; i ++) {
      sbs[i] = new StringBuilder();
      sbs[i].append("level ").append(i).append(":");
    }
    
    SkipList_Node cur = root;
    
    while (cur.getNext(0) != null) {
      cur = cur.getNext(0);
      for(int i = levels - 1; i >= cur.getNext().length; i --) {
        sbs[i].append("\t");
      }
      for(int i = cur.getNext().length - 1; i >= 0; i --) {
        if (cur.getNext(i) == null) {
          levels --;
        }
        sbs[i].append("\t").append(cur.getValue());
      }
    }
    
    for(int i = sbs.length - 1; i >= 0; i --) {
      sb.append(sbs[i]).append("\n");
    }
    
    return sb.toString();
  }
  	

	@Override
	public boolean insert(double value) {
		
		if(contains(value)) return false;
		
		int level = randomLevel();
		SkipList_Node[] update = new SkipList_Node[level];
		
		SkipList_Node currentNode = root;
		
		while(currentNode.getNext() != null) {
			SkipList_Node[] nextNodes = currentNode.getNext();
			
			int currentLevel = nextNodes.length-1;
			while(currentLevel >  -1) {
				
				if(nextNodes[currentLevel] != null) {
					if(nextNodes[currentLevel].getValue() > value) {
						if(currentLevel <= level) update[currentLevel] = currentNode;
						currentLevel--;
					} else {
						currentNode = currentNode.getNext(currentLevel);
						break;
					}
					
				} else {
					currentLevel--;
				}
			}
			
		}
		
		// currentNode holds position of preceding node
		SkipList_Node newNode = new SkipList_Node(value, level);
		
		newNode.setNext(0, currentNode.getNext(0));
		
		for(int i = 0; i < level; i++) {
			
			SkipList_Node prevNode = update[i];
			if(prevNode != null) {
				newNode.setNext(i, prevNode.getNext(i));
				prevNode.setNext(i, newNode);
			}
			
		}
		
		size++;
		return true;
	}
	
	@Override
	public boolean remove(double value) {
		// TODO Auto-generated method stub
		if(!contains(value)) return false;
		
		int level = 0;
		
		Map<Integer, SkipList_Node> nodesWithReferences = new HashMap<Integer, SkipList_Node>();
		
		SkipList_Node currentNode = root;
		
		while(currentNode.getNext() != null) {
			SkipList_Node[] nextNodes = currentNode.getNext();
			
			int currentLevel = nextNodes.length-1;
			while(currentLevel >  -1) {
				
				if(nextNodes[currentLevel] != null) {
					if(nextNodes[currentLevel].getValue() == value) {
						nodesWithReferences.put(currentLevel, currentNode);
						level++;
						currentLevel--;
					} else {
						currentNode = currentNode.getNext(currentLevel);
						break;
					}
				} else {
					currentLevel--;
				}
			}
			
		}
		
		// currentNode holds position of preceding node
		SkipList_Node nodeForDeletion = currentNode.getNext(0);
		
		for(int i = 0; i < level; i++) {
			nodesWithReferences.get(i).setNext(i, nodeForDeletion.getNext(i));
		}
		
		// see if level got smaller
		
		int highestLevel = maxHeight;
		
		while(highestLevel > -1) {
			if(root.getNext(highestLevel) != null) {
				break;
			} else {
				highestLevel--;
			}
		}
		
		this.level = highestLevel;
			
		size--;
		return true;
	}
	
	@Override
	public boolean contains(double value) {
		// TODO Auto-generated method stub
		
		if(empty()) return false;
		
		SkipList_Node currentNode = root;
		
		while(currentNode.getNext() != null) {
			SkipList_Node[] nextNodes = currentNode.getNext();
			
			int currentValue = nextNodes.length-1;
			while(currentValue > -1) {
								
				if(currentNode != null) {
					if(nextNodes[currentValue].getValue() > value) {
						currentValue--;
					} else if(nextNodes[currentValue].getValue() <  value){
						currentNode = currentNode.getNext(currentValue);
						break;
					} else {
						return true;
					}
				} else {
					currentValue--;
				}
			}
						
		}
		
		return false;
	}
	
	@Override
	public double findMin() {
		// TODO Auto-generated method stub
		if(empty()) return Double.NaN;
		
		return root.getNext(0).getValue();
	}
	
	@Override
	public double findMax() {
		// TODO Auto-generated method stub
		SkipList_Node currentNode = root;
		SkipList_Node nextNodes[] = root.getNext();
		
		while(nextNodes != null) {
			
			for(int i = nextNodes.length; i > -1; i--) {
				if(nextNodes[i] != null) {
					currentNode = nextNodes[i];
					break;
				}
			}
			
			nextNodes = currentNode.getNext();
		}
		
		return currentNode.getValue();
	}
	
	@Override
	public boolean empty() {
		// TODO Auto-generated method stub
		return size == 0;
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		root = null;
		size = 0;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}
	
	@Override
	public int level() {
		// TODO Auto-generated method stub
		if(empty()) return -1;
		
		return level;
	}
	
	@Override
	public int max() {
		// TODO Auto-generated method stub
		return maxHeight;
	}

}