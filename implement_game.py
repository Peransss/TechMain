with open('app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt', 'r') as f:
    content = f.read()

# Add imports
imports = '''
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.techmain.ui.theme.NeonHackerPrimary
'''
content = content.replace('import androidx.compose.foundation.layout.width', 'import androidx.compose.foundation.layout.width' + imports)

# Update Game screen to include AsyncImage
image_section = '''
        if (currentQuestion.imageUrl != null) {
            AsyncImage(
                model = currentQuestion.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp).border(2.dp, NeonHackerPrimary)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
'''
content = content.replace(
    'if (currentQuestion != null) {',
    'if (currentQuestion != null) {\n' + image_section
)

with open('app/src/main/java/com/example/techmain/ui/battle/BattleGameScreen.kt', 'w') as f:
    f.write(content)
print("BattleGameScreen updated")
