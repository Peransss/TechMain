with open('app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt', 'r') as f:
    content = f.read()

# Add imports
imports = '''
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import com.example.techmain.firebase.CustomQuiz
import com.example.techmain.ui.theme.NeonHackerBackground
import com.example.techmain.ui.theme.NeonHackerBorder
import com.example.techmain.ui.theme.NeonHackerPrimary
'''
content = content.replace('import androidx.compose.material3.TextButton', 'import androidx.compose.material3.TextButton' + imports)

# Update LobbyContent
featured_quiz_section = '''
        val featuredQuizzes by viewModel.featuredQuizzes.collectAsState()
        if (featuredQuizzes.isNotEmpty()) {
            Text("Featured Quizzes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NeonHackerPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().height(140.dp)
            ) {
                items(featuredQuizzes) { quiz ->
                    Card(
                        modifier = Modifier.size(width = 200.dp, height = 120.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonHackerPrimary),
                        colors = CardDefaults.cardColors(containerColor = NeonHackerBackground)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(quiz.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("${quiz.questions.size} Questions", color = NeonHackerPrimary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
'''
content = content.replace(
    'Text("Pilih Kategori",',
    featured_quiz_section + '\n        Text("Pilih Kategori",'
)

with open('app/src/main/java/com/example/techmain/ui/battle/BattleLobbyScreen.kt', 'w') as f:
    f.write(content)
print("BattleLobbyScreen updated")
