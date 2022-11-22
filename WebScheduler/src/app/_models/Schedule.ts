export interface ISchedule {
    id: number;
    valid: boolean;
    month: Date;
    generatedDateTime: Date;
    generatedByUser: String;
    userDuties: any[];
}