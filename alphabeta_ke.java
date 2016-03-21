import java.util.*;
import java.awt.Point;

public class alphabeta_ke extends AIModule
{

    public static int[][] evalTable = {


    		{1, 1, 1, 2, 1, 1, 1},
    		{1, 1, 1, 3, 1, 1, 1},
    		{1, 1, 1, 4, 1, 1, 1},
    		{1, 1, 1, 4, 1, 1, 1},
    		{1, 1, 1, 3, 1, 1, 1},
    		{1, 1, 1, 2, 1, 1, 1}

    };

    //private int optimalMove = 0;
    public int[] branchOrderingForMax = {3, 4, 2, 5, 1, 6, 0};
    public int[] branchOrderingForMin = {0, 6, 1, 5, 2, 4, 3};


    @Override
	public void getNextMove(final GameStateModule state)
	{
		int depth = 1;
		int[] bestMoveAndValue = {0,0};



		final GameStateModule game = state.copy();

		// Start simulating games! Continue until told to stop.
		while(!terminate)
		{
			if(depth < 5)
				bestMoveAndValue = miniMax(game, depth, -1, game.getActivePlayer(), Integer.MIN_VALUE, Integer.MAX_VALUE);
			depth++;
			
			chosenMove = bestMoveAndValue[0];

		}
		
	} // end of getNextMove



	private int[] miniMax(final GameStateModule game, int depth, int selectedColumn, int player, int alpha, int beta){
		
		// check game is over or not
		int legalMove = 0;
		for(int i = 0; i < 7 ; i++){
			if(game.canMakeMove(i))
				legalMove++;
			//System.out.print("chcek1\n");
		}

		// if legalMove = 0, it means no legal move
		// i assume game.isGameOver() is true when 4 X or O in a row, then game end
		if(game.isGameOver() || legalMove == 0 || depth == 0){
			int temp[] = {0,0};
			if(player == 2)
				player = 1;
			else
				player = 2;

			temp[1] = evalFcn(game, selectedColumn, player);
			return temp;
		}
		
			/************** player 1 always max **************/
			if(player == 1){
				int[] optimalMoveAndValue = {0,Integer.MIN_VALUE};
				// int bestValue = Integer.MIN_VALUE;
				int currentValue = Integer.MIN_VALUE;
				int temp[] = {0,0};

				for(int col : branchOrderingForMax){
					
					if(game.canMakeMove(col)){
						game.makeMove(col);
						temp = miniMax(game, depth - 1, col, 2, alpha, beta);
						currentValue = temp[1];

						// max try to find the highest score and best move
						if(currentValue > optimalMoveAndValue[1]){
							optimalMoveAndValue[1] = currentValue;
							optimalMoveAndValue[0] = col;
						}

						game.unMakeMove();

						if(currentValue > alpha){
							alpha = currentValue;
							optimalMoveAndValue[0] = col;
						}

						if(beta <= alpha)
							break;


					}
					else{

						currentValue = Integer.MIN_VALUE;
						// this move is illegal
						// ignore this move / ignore this node from game tree view

					}
			}

				if(terminate)
					System.out.print("terminate in MAX\n");

				return optimalMoveAndValue;

		}


			/************** player 2 always min *************/
			else{
				int[] optimalMoveAndValue = {0,Integer.MAX_VALUE};
				// int bestValue = Integer.MAX_VALUE;
				int currentValue = Integer.MAX_VALUE;
				int temp[] = {0,0};

				for(int col : branchOrderingForMin){

					if(game.canMakeMove(col)){
						game.makeMove(col);
						temp = miniMax(game, depth - 1, col, 1, alpha, beta);
						currentValue = temp[1];

						// min try to find the lowest score and best move
						if(currentValue < optimalMoveAndValue[1]){
							optimalMoveAndValue[1] = currentValue;
							optimalMoveAndValue[0] = col;
						}

						game.unMakeMove();

						if(currentValue < beta){
							beta = currentValue;
							optimalMoveAndValue[0] = col;
						}

						if(beta <= alpha)
							break;


					}
					else{

						currentValue = Integer.MAX_VALUE;
						// this move is illegal
						// ignore this move / ignore this node from game tree view

					}


				}

				if(terminate)
					System.out.print("terminate in MIN\n");

				return optimalMoveAndValue;

			}

	} // end of miniMax

	private int evalFcn(final GameStateModule game, int selectedColumn, int player){

		//score[0] is player 1 score, score[1] is player 2 score
		int score[] = {0,0};
		
			/************ Assume player 1 is O; player 2 is X ***************/
			for(int x = 0; x < 7; x++){

				for(int y = 0; y < 6; y++){

					/***********************single cell weight************************/

					if(game.getAt(x,y) == 1){
						score[0] += evalTable[y][x];
					}
					else if(game.getAt(x,y) == 2){
						score[1] -= evalTable[y][x];
					}
					else{
						//ignore
					}

					/***********************two in a row************************/

					//two in a row horizontally
					if(x+1 < 7){
						// O O _ _
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y) == 1 && game.getAt(x+2,y) == 0 && game.getAt(x+3,y) == 0 && (x <= 3)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y][x+1]) * 50;
						}
						// X X _ _
						if(game.getAt(x,y) == 2 && game.getAt(x+1,y) == 2 && game.getAt(x+2,y) == 0 && game.getAt(x+3,y) == 0 && (x <= 3)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y][x+1]) * 50;
						}
						// _ O O _
						if(game.getAt(x-1,y) == 0 && game.getAt(x,y) == 1 && game.getAt(x+1,y) == 1 && game.getAt(x+2,y) == 0 && (x >= 1 && x <= 4)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y][x+1]) * 50;
						}
						// _ X X _
						if(game.getAt(x-1,y) == 0 && game.getAt(x,y) == 2 && game.getAt(x+1,y) == 2 && game.getAt(x+2,y) == 0 && (x >= 1 && x <= 4)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y][x+1]) * 50;
						}
						// _ _ O O
						if(game.getAt(x-2,y) == 0 && game.getAt(x-1,y) == 0 && game.getAt(x,y) == 1 && game.getAt(x+1,y) == 1 && (x >= 2 && x <= 5)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y][x+1]) * 50;
						}
						// _ _ X X
						if(game.getAt(x-2,y) == 0 && game.getAt(x-1,y) == 0 && game.getAt(x,y) == 2 && game.getAt(x+1,y) == 2 && (x >= 2 && x <= 5)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y][x+1]) * 50;
						}
						
					}

					//two in a row vertically
					if(y+1 < 6 ){

						// (bottom) O O _ _ (top)
						if(game.getAt(x,y) == 1 && game.getAt(x,y+1) == 1 && game.getAt(x,y+2) == 0 && game.getAt(x,y+3) == 0 && (y <= 2)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x]) * 50;
						}
						// X X _ _
						if(game.getAt(x,y) == 2 && game.getAt(x,y+1) == 2 && game.getAt(x,y+2) == 0 && game.getAt(x,y+3) == 0 && (y <= 2)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x]) * 50;
						}
						// _ O O _
						if(game.getAt(x,y-1) == 0 && game.getAt(x,y) == 1 && game.getAt(x,y+1) == 1 && game.getAt(x,y+2) == 0 && (y >= 1 && y <= 3)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x]) * 50;
						}
						// _ X X _
						if(game.getAt(x,y-1) == 0 && game.getAt(x,y) == 2 && game.getAt(x,y+1) == 2 && game.getAt(x,y+2) == 0 && (y >= 1 && y <= 3)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x]) * 50;
						}
						// _ _ O O
						if(game.getAt(x,y-2) == 0 && game.getAt(x,y-1) == 0 && game.getAt(x,y) == 1 && game.getAt(x,y+1) == 1 && (y >= 2 && y<= 4)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x]) * 50;
						}
						// _ _ X X
						if(game.getAt(x,y-2) == 0 && game.getAt(x,y-1) == 0 && game.getAt(x,y) == 2 && game.getAt(x,y+1) == 2 && (y >= 2 && y<= 4)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x]) * 50;
						}
						
					}

					//two in a row diagonally (left to right up)
					if((x+1 < 7) && (y+1 < 6)){
						// (bottom left) O O _ _ (top right)
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y+1) == 1 && game.getAt(x+2,y+2) == 0 && game.getAt(x+3,y+3) == 0 && (x <= 3 && y <= 2)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x+1]) * 50;
						}
						// X X _ _
						if(game.getAt(x,y) == 2 && game.getAt(x+1,y+1) == 2 && game.getAt(x+2,y+2) == 0 && game.getAt(x+3,y+3) == 0 && (x <= 3 && y <= 2)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x+1]) * 50;
						}
						// _ O O _
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y+1) == 1 && game.getAt(x+2,y+2) == 1 && game.getAt(x+3,y+3) == 0 && (x >= 1 && x <= 4 && y>=1 && y <= 3)){
							score[0] = score[0] + (evalTable[y+1][x+1] + evalTable[y+2][x+2]) * 50;
						}
						// _ X X _
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y+1) == 2 && game.getAt(x+2,y+2) == 2 && game.getAt(x+3,y+3) == 0 && (x >= 1 && x <= 4 && y>=1 && y <= 3)){
							score[1] = score[1] - (evalTable[y+1][x+1] + evalTable[y+2][x+2]) * 50;
						}
						// _ _ O O
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y+1) == 0 && game.getAt(x+2,y+2) == 1 && game.getAt(x+3,y+3) == 1 && (x >= 2 && x <= 5 && y>=2 && y <= 4)){
							score[0] = score[0] + (evalTable[y+2][x+2] + evalTable[y+3][x+3]) * 50;
						}
						// _ _ X X
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y+1) == 0 && game.getAt(x+2,y+2) == 2 && game.getAt(x+3,y+3) == 2 && (x >= 2 && x <= 5 && y>=2 && y <= 4)){
							score[1] = score[1] - (evalTable[y+2][x+2] + evalTable[y+3][x+3]) * 50;
						}
						
					}

					//two in a row diagonally (left to right down)
					if((y > 0) && (x+1 < 6)){
						// (top left) O O _ _ (bottom right)
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y-1) == 1 && game.getAt(x+2,y-2) == 0 && game.getAt(x+3,y-3) == 0 && (x <= 3 && y >= 3)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y-1][x+1]) * 50;
						}
						// X X _ _
						if(game.getAt(x,y) == 2 && game.getAt(x+1,y-1) == 2 && game.getAt(x+2,y-2) == 0 && game.getAt(x+3,y-3) == 0 && (x <= 3 && y >= 3)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y-1][x+1]) * 50;
						}
						// _ O O _
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y-1) == 1 && game.getAt(x+2,y-2) == 1 && game.getAt(x+3,y-3) == 0 && (x >= 1 && x <= 4 && y >= 2 && y <= 4)){
							score[0] = score[0] + (evalTable[y-1][x+1] + evalTable[y-2][x+2]) * 50;
						}
						// _ X X _
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y-1) == 2 && game.getAt(x+2,y-2) == 2 && game.getAt(x+3,y-3) == 0 && (x >= 1 && x <= 4 && y >= 2 && y <= 4)){
							score[1] = score[1] - (evalTable[y-1][x+1] + evalTable[y-2][x+2]) * 50;
						}
						// _ _ O O
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y-1) == 0 && game.getAt(x+2,y-2) == 1 && game.getAt(x+3,y-3) == 1 && (x >= 2 && x <= 5 && y >= 1 && y <= 3)){
							score[0] = score[0] + (evalTable[y-2][x+2] + evalTable[y-3][x+3]) * 50;
						}
						// _ _ X X
						if(game.getAt(x,y) == 0 && game.getAt(x+1,y-1) == 0 && game.getAt(x+2,y-2) == 2 && game.getAt(x+3,y-3) == 2 && (x >= 2 && x <= 5 && y >= 1 && y <= 3)){
							score[1] = score[1] - (evalTable[y-2][x+2] + evalTable[y-3][x+3]) * 50;
						}
						
					}

					/***********************three in a row************************/

					//three in a row horizontally
					if(x+2 < 7){
						// O O O _
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y) == 1 && game.getAt(x+2,y) == 1 && game.getAt(x+3,y) == 0 && (x <= 3)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y][x+1] + evalTable[y][x+2]) * 50000;
						}
						// X X X _
						if(game.getAt(x,y) == 2 && game.getAt(x+1,y) == 2 && game.getAt(x+2,y) == 2 && game.getAt(x+3,y) == 0 && (x <= 3)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y][x+1] + evalTable[y][x+2]) * 50000;
						}
						// _ O O O
						if(game.getAt(x-1,y) == 0 && game.getAt(x,y) == 1 && game.getAt(x+1,y) == 1 && game.getAt(x+2,y) == 1 && (x >= 1 && x <= 4)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y][x+1] + evalTable[y][x+2]) * 50000;
						}
						// _ X X X
						if(game.getAt(x-1,y) == 0 && game.getAt(x,y) == 2 && game.getAt(x+1,y) == 2 && game.getAt(x+2,y) == 2 && (x >= 1 && x <= 4)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y][x+1] + evalTable[y][x+2])  * 50000;
						}
						
					}

					//three in a row vertically
					if(y+2 < 6){
						// (bottom) O O O _ (top)
						if(game.getAt(x,y) == 1 && game.getAt(x,y+1) == 1 && game.getAt(x,y+2) == 1 && game.getAt(x,y+3) == 0 && (y <= 2)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x] + evalTable[y+2][x]) * 50000;
						}
						// X X X _
						if(game.getAt(x,y) == 2 && game.getAt(x,y+1) == 2 && game.getAt(x,y+2) == 2 && game.getAt(x,y+3) == 0 && (y <= 2)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x] + evalTable[y+2][x]) * 50000;
						}
						// _ O O O
						if(game.getAt(x,y) == 0 && game.getAt(x,y+1) == 1 && game.getAt(x,y+2) == 1 && game.getAt(x,y+3) == 1 && (y >= 1 && y <= 3)){
							score[0] = score[0] + (evalTable[y+1][x] + evalTable[y+2][x] + evalTable[y+3][x]) * 50000;
						}
						// _ X X X
						if(game.getAt(x,y) == 0 && game.getAt(x,y+1) == 2 && game.getAt(x,y+2) == 2 && game.getAt(x,y+3) == 2 && (y >= 1 && y <= 3)){
							score[1] = score[1] - (evalTable[y+1][x] + evalTable[y+2][x] + evalTable[y+3][x]) * 50000;
						}
					
					}

					//three in a row diagonally (left to right up)
					if((x+2 < 7) && (y+2 < 6)){
						// (bottom left) O O O _ (top right)
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y+1) == 1 && game.getAt(x+2,y+2) == 1 && game.getAt(x+3,y+3) == 0 && (x <= 3 && y <= 2)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x+1] + evalTable[y+2][x+2]) * 50000;
						}
						// X X X _
						if(game.getAt(x,y) == 2 && game.getAt(x+1,y+1) == 2 && game.getAt(x+2,y+2) == 2 && game.getAt(x+3,y+3) == 0 && (x <= 3 && y <= 2)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x+1] + evalTable[y+2][x+2]) * 50000;
						}
						// _ O O O
						if(game.getAt(x-1,y-1) == 0 && game.getAt(x,y) == 1 && game.getAt(x+1,y+1) == 1 && game.getAt(x+2,y+2) == 1 && (x >= 1 && x <= 4 && y>=1 && y <= 3)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y+1][x+1] + evalTable[y+2][x+2]) * 50000;
						}
						// _ X X X
						if(game.getAt(x-1,y-1) == 0 && game.getAt(x,y) == 2 && game.getAt(x+1,y+1) == 2 && game.getAt(x+2,y+2) == 2 && (x >= 1 && x <= 4 && y>=1 && y <= 3)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y+1][x+1] + evalTable[y+2][x+2]) * 50000;
						}
						
					}

					//three in a row diagonally (left to right down)
					if((y > 1) && (x+2 < 6)){
						// (top left) O O O _ (bottom right)
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y-1) == 1 && game.getAt(x+2,y-2) == 1 && game.getAt(x+3,y-3) == 0 && (x <= 3 && y >= 3 && y <= 5)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y-1][x+1] + evalTable[y-2][x+2]) * 50000;
						}
						// X X X _
						if(game.getAt(x,y) == 2 && game.getAt(x+1,y-1) == 2 && game.getAt(x+2,y-2) == 2 && game.getAt(x+3,y-3) == 0 && (x <= 3 && y >= 3 && y <= 5)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y-1][x+1] + evalTable[y-2][x+2]) * 50000;
						}
						// _ O O O
						if(game.getAt(x-1,y+1) == 0 && game.getAt(x,y) == 1 && game.getAt(x+1,y-1) == 1 && game.getAt(x+2,y-2) == 1 && (x >= 1 && x <= 4 && y>=1 && y <= 3)){
							score[0] = score[0] + (evalTable[y][x] + evalTable[y-1][x+1] + evalTable[y-2][x+2]) * 50000;
						}
						// _ X X X
						if(game.getAt(x-1,y+1) == 0 && game.getAt(x,y) == 2 && game.getAt(x+1,y-1) == 2 && game.getAt(x+2,y-2) == 2 && (x >= 1 && x <= 4 && y>=1 && y <= 3)){
							score[1] = score[1] - (evalTable[y][x] + evalTable[y-1][x+1] + evalTable[y-2][x+2]) * 50000;
						}
						
					}

					/***********************four in a row************************/

					//four in a row horizontally
					if(x+3 < 7){
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y) == 1 && game.getAt(x+2,y) == 1 && game.getAt(x+3,y) == 1){
							return Integer.MAX_VALUE;
							// fourInARowCounter[0]++;
						}
						else if(game.getAt(x,y) == 2 && game.getAt(x+1,y) == 2 && game.getAt(x+2,y) == 2 && game.getAt(x+3,y) == 2){
							return Integer.MIN_VALUE;
							// fourInARowCounter[1]++;
						}
						else{
							//no four in a row horizontally
						}
					}

					//four in a row vertically
					if(y+3 < 6){
						if(game.getAt(x,y) == 1 && game.getAt(x,y+1) == 1 && game.getAt(x,y+2) == 1 && game.getAt(x,y+3) == 1){
							return Integer.MAX_VALUE;
							// fourInARowCounter[0]++;
						}
						else if(game.getAt(x,y) == 2 && game.getAt(x,y+1) == 2 && game.getAt(x,y+2) == 2 && game.getAt(x,y+3) == 2){
							return Integer.MIN_VALUE;
							// fourInARowCounter[1]++;
						}
						else{
							//no four in a row vertically
						}
					}

					//four in a row diagonally (left to right up)
					if((x+3 < 7) && (y+3 < 6)){
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y+1) == 1 && game.getAt(x+2,y+2) == 1 && game.getAt(x+3,y+3) == 1){
							return Integer.MAX_VALUE;
							// fourInARowCounter[0]++;
						}
						else if(game.getAt(x,y) == 2 && game.getAt(x+1,y+1) == 2 && game.getAt(x+2,y+2) == 2 && game.getAt(x+3,y+3) == 2){
							return Integer.MIN_VALUE;
							// fourInARowCounter[1]++;
						}
						else{
							//no four in a row diagonally (left to right up)
						}
					}

					//four in a row diagonally (left to right down)
					if((y > 2) && (x < 4)){
						if(game.getAt(x,y) == 1 && game.getAt(x+1,y-1) == 1 && game.getAt(x+2,y-2) == 1 && game.getAt(x+3,y-3) == 1){
							return Integer.MAX_VALUE;
							// fourInARowCounter[0]++;
						}
						else if(game.getAt(x,y) == 2 && game.getAt(x+1,y-1) == 2 && game.getAt(x+2,y-2) == 2 && game.getAt(x+3,y-3) == 2){
							return Integer.MIN_VALUE;
							// fourInARowCounter[1]++;
						}
						else{
							//no four in a row diagonally (left to right down)
						}
					}

				}

			}

		return score[0] + score[1];

	} // end of evalFcn

}