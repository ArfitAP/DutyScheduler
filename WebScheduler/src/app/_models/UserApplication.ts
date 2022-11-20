export interface IUserApplication {
    id: number;
    active: boolean;
    month: Date;
    grouped: boolean;
    user_id: number;
    applicationDays: any[];
}