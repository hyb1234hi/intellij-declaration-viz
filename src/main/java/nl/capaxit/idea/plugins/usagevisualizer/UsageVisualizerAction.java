package nl.capaxit.idea.plugins.usagevisualizer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple action for testing various Idea plugin parts. Action should be invoked when hovering over
 * an element in the editor, perhaps by holding the control button.
 * <p>
 * Created by jamiecraane on 25/10/2016.
 */
public class UsageVisualizerAction extends AnAction {
    // todo there must be a better way to determine this offset.
    public static final int FIXED_X_OFFSET = 100;

    @Override
    public void actionPerformed(final AnActionEvent e) {
        doWork(e);
    }

    private void doWork(final AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }

        final PsiElement elementAtCaret = findElementAtCaret(project, editor);
        if (elementAtCaret == null) {
            return;
        }

        final PsiReferenceExpression parent = PsiTreeUtil.getParentOfType(elementAtCaret, PsiReferenceExpression.class);
        if (parent != null) {
            findAndDrawUsagesLines(editor, parent);
        }
    }

    private void findAndDrawUsagesLines(final Editor editor, final PsiReferenceExpression parent) {
        final int verticalScrollOffset = editor.getScrollingModel().getVerticalScrollOffset();
        final int elementXOffset = FIXED_X_OFFSET;

        final PsiElement declaration = parent.resolve();

        final Point declarationPosition = editor.visualPositionToXY(editor.offsetToVisualPosition(declaration.getTextOffset()));
        final Point declarationPoint = new Point(declarationPosition.x + elementXOffset, declarationPosition.y - verticalScrollOffset);
        final Collection<PsiReference> references = ReferencesSearch.search(declaration).findAll();
        final List<LineSpec> lines = references.stream()
                .map(reference -> createUsageLineSpec(editor, verticalScrollOffset, elementXOffset, declarationPoint, reference))
                .collect(Collectors.toList());

        drawLines(lines, editor);
    }

    @NotNull
    private LineSpec createUsageLineSpec(final Editor editor, final int verticalScrollOffset, final int elementXOffset, final Point declarationPoint, final PsiReference reference) {
        final PsiElement element = reference.getElement();
        final Point elementPosition = editor.visualPositionToXY(editor.offsetToVisualPosition(element.getTextOffset()));
        return LineSpec.create(declarationPoint, new Point(elementPosition.x + elementXOffset, elementPosition.y - verticalScrollOffset));
    }

    private PsiElement findElementAtCaret(final Project project, final Editor editor) {
        final CaretModel caretModel = editor.getCaretModel();
        final Document document = editor.getDocument();
        final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        return psiFile.findElementAt(caretModel.getOffset());
    }

    private void drawLines(final List<LineSpec> lines, final Editor editor) {
        final Graphics graphics = editor.getComponent().getGraphics();
        graphics.setColor(new Color(131, 142, 255, 128));
        ((Graphics2D) graphics).setStroke(new BasicStroke(2));
        lines.forEach(line -> graphics.drawLine(line.getStart().x, line.getStart().y, line.getEnd().x, line.getEnd().y));
    }

    @Override
    public void update(final AnActionEvent e) {
        doWork(e);
    }
}
