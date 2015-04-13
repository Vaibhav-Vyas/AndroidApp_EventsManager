class CreateEvents < ActiveRecord::Migration
  def change
    create_table :events do |t|
      t.string :name
      t.string :location
      t.string :description
      t.string :startTime
      t.string :endTime
      t.string :imageUrl
      t.boolean :public
      t.string :organization
      t.string :type

      t.timestamps null: false
    end
  end
end
