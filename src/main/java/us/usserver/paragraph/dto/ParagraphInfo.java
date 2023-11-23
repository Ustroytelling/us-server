package us.usserver.paragraph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.paragraph.paragraphEnum.ParagraphStatus;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphInfo {
    private List<ParagraphSelected> selectedList;
    private List<ParagraphUnSelected> unSelectedList;
}
