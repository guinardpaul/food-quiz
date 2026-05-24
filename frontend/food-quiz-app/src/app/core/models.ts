export interface QuizDto {
  quizId: string;
}

export interface QuestionDto {
  label: string;
  foodName: string;
  imageUrl: string;
  portionDescription: string;
  proposedAnswers: string[];
}

export interface FeedbackDto {
  correct: boolean;
  correctAnswer: string;
  equivalents: { breadSlices: number; sugarCubes: number };
  glycemicImpact: 'LOW' | 'MEDIUM' | 'HIGH';
  hasNextQuestion: boolean;
}

export interface QuizResultDto {
  score: number;
  total: number;
}
