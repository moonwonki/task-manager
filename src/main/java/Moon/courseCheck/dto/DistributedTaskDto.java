package Moon.courseCheck.dto;

import Moon.courseCheck.domain.DistributedTask;
import Moon.courseCheck.domain.OriginTask;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DistributedTaskDto {
    private Long distributedId;
    private Long originId;
    private String content;
    private String title;
    private String status;


    public static DistributedTaskDto getInstance(DistributedTask distributedTask, OriginTask originTask){
        DistributedTaskDto dto = new DistributedTaskDto();
        dto.setDistributedId(distributedTask.getId());
        dto.setOriginId(originTask.getId());
        dto.setContent(originTask.getContent());
        dto.setTitle(originTask.getTitle());
        dto.setStatus(distributedTask.getStatus());
        return dto;
    }

}
